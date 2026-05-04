package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.cboard.owlswap.owlswap_backend.model.User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        String roleAuthority = "ROLE_" + user.getRole().name();
        var authorities = org.springframework.security.core.authority.AuthorityUtils.createAuthorityList(roleAuthority);


        return new com.cboard.owlswap.owlswap_backend.security.AppUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}