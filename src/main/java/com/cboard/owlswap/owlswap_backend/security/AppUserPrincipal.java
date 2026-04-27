package com.cboard.owlswap.owlswap_backend.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
public class AppUserPrincipal implements UserDetails{
    private final Integer userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public AppUserPrincipal(Integer userId, String username, String password, Collection<? extends GrantedAuthority> authorities)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }


    public Integer getUserId() {
        return userId;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
