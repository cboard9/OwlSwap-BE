package com.cboard.owlswap.owlswap_backend.security;

import com.cboard.owlswap.owlswap_backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessExpMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-exp-ms}") long accessExpMs
    )
    {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMs;
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) //unique user identity
                .claim("id", user.getUserId()) // user id
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token).getSubject();
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Integer extractUserId(String token)
    {

        Claims claims = extractAllClaims(token);

        Object id = claims.get("id");
        if (id instanceof Integer i)
            return i;
        if (id instanceof Number n)
            return n.intValue();
        if (id instanceof String s)
            return Integer.parseInt(s);

        throw new JwtException("Invalid is claim type");

    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}