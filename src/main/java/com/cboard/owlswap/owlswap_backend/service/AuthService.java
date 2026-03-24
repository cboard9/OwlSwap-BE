package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.EmailVerificationDao;
import com.cboard.owlswap.owlswap_backend.dao.RefreshTokenDao;
import com.cboard.owlswap.owlswap_backend.model.Dto.AuthResponse;
import com.cboard.owlswap.owlswap_backend.model.Dto.LoginRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.SignupRequest;
import com.cboard.owlswap.owlswap_backend.security.JwtUtil;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import com.cboard.owlswap.owlswap_backend.dao.EmailVerificationDao;
import com.cboard.owlswap.owlswap_backend.security.EmailVerificationToken;

import com.cboard.owlswap.owlswap_backend.security.RefreshToken;
import com.cboard.owlswap.owlswap_backend.security.RefreshTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired private RefreshTokenDao refreshTokenDao;
    @Autowired private EmailVerificationDao emailVerificationDao;
    @Autowired private EmailService emailService;

    @Value("${app.email-verification.exp-hours}")
    private long emailVerificationExpHours;

    @Value("${app.refresh.cookie-name}") private String refreshCookieName;
    @Value("${app.refresh.exp-days}") private long refreshExpDays;
    @Value("${app.refresh.cookie-secure}") private boolean refreshCookieSecure;

    @Value("${app.backend.base-url}")
    private String backendBaseUrl;


    public ResponseEntity<?> registerUser(SignupRequest request) {

        if(request.getUsername() == null || request.getUsername().isBlank() || request.getPassword() == null || request.getPassword().isBlank())
            return ResponseEntity.badRequest().body("Username and password can not be blank.");

        if(request.getFirstName() == null || request.getFirstName().isBlank() || request.getLastName() == null || request.getLastName().isBlank())
            return ResponseEntity.badRequest().body("Must enter first and last name.");

        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        if(request.getEmail() == null || request.getEmail().isBlank())
            return ResponseEntity.badRequest().body("Must enter an email.");

        if (!request.getEmail().toLowerCase().endsWith("@westfield.ma.edu")) {
            return ResponseEntity.badRequest().body("Email must be a Westfield student address.");
        }

        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }


        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEmailVerified(false);

        userDao.save(user);

        String rawVerificationToken = RefreshTokenUtil.generateRawToken();
        String verificationHash = RefreshTokenUtil.hash(rawVerificationToken);

        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setUser(user);
        evt.setTokenHash(verificationHash);
        evt.setExpiresAt(Instant.now().plus(emailVerificationExpHours, ChronoUnit.HOURS));

        emailVerificationDao.save(evt);

        //For backend-direct verification, change later
        String verifyUrl = backendBaseUrl + "/api/auth/verify-email?token=" + rawVerificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verifyUrl);


        return ResponseEntity.ok("User registered successfully.");
    }

    public ResponseEntity<?> loginUser(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank()
                    || request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body("Username and password can not be blank.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            Optional<User> userOpt = userDao.findByUsername(request.getUsername());
            if (userOpt.isEmpty())
                return ResponseEntity.status(400).body("Username not found.");

            User user = userOpt.get();

            // 1) ACCESS TOKEN (keep your existing method for now)
            String accessToken = jwtUtil.generateAccessToken(user); // later we will shorten exp + rename

            // 2) REFRESH TOKEN (opaque)
            String rawRefresh = RefreshTokenUtil.generateRawToken();
            String hash = RefreshTokenUtil.hash(rawRefresh);

            RefreshToken rt = new RefreshToken();
            rt.setUser(user);
            rt.setTokenHash(hash);
            rt.setExpiresAt(Instant.now().plus(refreshExpDays, ChronoUnit.DAYS));
            rt.setIp(httpRequest.getRemoteAddr());
            rt.setUserAgent(httpRequest.getHeader("User-Agent"));

            refreshTokenDao.save(rt);

            // 3) Set refresh token cookie (HttpOnly)
            ResponseCookie cookie = ResponseCookie.from(refreshCookieName, rawRefresh)
                    .httpOnly(true)
                    .secure(refreshCookieSecure)     // false on localhost http, true in prod https
                    .sameSite("Lax")
                    .path("/api/auth/refresh")       // only send cookie to refresh endpoint
                    .maxAge(Duration.ofDays(refreshExpDays))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(accessToken));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }

    public ResponseEntity<?> refresh(HttpServletRequest request)
    {
        // 1) Read refresh cookie
        String rawRefresh = readCookie(request, refreshCookieName);
        if (rawRefresh == null || rawRefresh.isBlank()) {
            return ResponseEntity.status(401).body("Missing refresh token.");
        }

        // 2) Hash and lookup in DB
        String hash = RefreshTokenUtil.hash(rawRefresh);

        RefreshToken existing = refreshTokenDao.findByTokenHash(hash).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(401).body("Invalid refresh token.");
        }

        // 3) Validate token state
        if (existing.isRevoked() || existing.isExpired()) {
            return ResponseEntity.status(401).body("Refresh token expired or revoked.");
        }

        // 4) Rotate: revoke old
        existing.setRevokedAt(Instant.now());
        existing.setLastUsedAt(Instant.now());
        refreshTokenDao.save(existing);

        User user = existing.getUser();

        // 5) Issue new access token
        String newAccessToken = jwtUtil.generateAccessToken(user); // for now keep existing; later shorten exp

        // 6) Create new refresh token row
        String newRawRefresh = RefreshTokenUtil.generateRawToken();
        String newHash = RefreshTokenUtil.hash(newRawRefresh);

        RefreshToken newRt = new RefreshToken();
        newRt.setUser(user);
        newRt.setTokenHash(newHash);
        newRt.setExpiresAt(Instant.now().plus(refreshExpDays, ChronoUnit.DAYS));
        newRt.setIp(request.getRemoteAddr());
        newRt.setUserAgent(request.getHeader("User-Agent"));
        refreshTokenDao.save(newRt);

        // 7) Set refreshed cookie
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, newRawRefresh)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(refreshExpDays))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(newAccessToken));
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName()))
                return c.getValue();
        }
        return null;
    }

    public ResponseEntity<?> logout(HttpServletRequest request)
    {
        String rawRefresh = readCookie(request, refreshCookieName);
        if (rawRefresh != null && !rawRefresh.isBlank()) {
            String hash = RefreshTokenUtil.hash(rawRefresh);
            refreshTokenDao.findByTokenHash(hash).ifPresent(rt -> {
                rt.setRevokedAt(Instant.now());
                refreshTokenDao.save(rt);
            });
        }

        // clear cookie
        ResponseCookie clear = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .body("Logged out.");

    }

    public ResponseEntity<?> verifyEmail(String rawToken)
    {
        if (rawToken == null || rawToken.isBlank()) {
            return ResponseEntity.badRequest().body("Missing verification token.");
        }

        String hash = RefreshTokenUtil.hash(rawToken);

        EmailVerificationToken evt = emailVerificationDao.findByTokenHash(hash)
                .orElse(null);

        if (evt == null) {
            return ResponseEntity.status(400).body("Invalid verification token.");
        }

        if (evt.isUsed()) {
            return ResponseEntity.status(400).body("Verification token has already been used.");
        }

        if (evt.isExpired()) {
            return ResponseEntity.status(400).body("Verification token has expired.");
        }

        User user = evt.getUser();
        user.setEmailVerified(true);
        userDao.save(user);

        evt.setUsedAt(Instant.now());
        emailVerificationDao.save(evt);

        return ResponseEntity.ok("Email verified successfully.");

    }

    public ResponseEntity<?> resendVerification(String email)
    {
        User user = userDao.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.ok("If that email exists, a verification email has been sent.");
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email is already verified.");
        }

        String rawVerificationToken = RefreshTokenUtil.generateRawToken();
        String verificationHash = RefreshTokenUtil.hash(rawVerificationToken);

        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setUser(user);
        evt.setTokenHash(verificationHash);
        evt.setExpiresAt(Instant.now().plus(emailVerificationExpHours, ChronoUnit.HOURS));

        emailVerificationDao.save(evt);

        String verifyUrl = backendBaseUrl + "/api/auth/verify-email?token=" + rawVerificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verifyUrl);

        return ResponseEntity.ok("Verification email re-sent.");
    }
}