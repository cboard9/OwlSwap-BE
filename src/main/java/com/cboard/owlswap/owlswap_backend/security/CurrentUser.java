package com.cboard.owlswap.owlswap_backend.security;

import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private final UserDao userDao;

    public CurrentUser(UserDao userDao)
    {
        this.userDao = userDao;
    }

/*    public String username()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthenticated");
        }
        return auth.getName(); // username
    }

    public Integer userId() {
        String username = username();
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username));
        return user.getUserId();
    }*/

    public AppUserPrincipal principal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthenticated");
        }
        Object p = auth.getPrincipal();
        if (!(p instanceof AppUserPrincipal principal)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthenticated");
        }
        return principal;
    }

    public Integer userId() {
        return principal().getUserId();
    }

    public String username() {
        return principal().getUsername();
    }

    public User user() {
        Integer id = userId();
        return userDao.findById(id)
                .orElseThrow(() ->
                        new org.springframework.security.core.userdetails.UsernameNotFoundException(
                                "Authenticated user not found in database. id=" + id
                        )
                );
    }

}
