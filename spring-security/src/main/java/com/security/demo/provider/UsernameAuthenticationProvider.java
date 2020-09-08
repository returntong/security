package com.security.demo.provider;

import com.security.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public class UsernameAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserService userService;

    public UsernameAuthenticationProvider() {
        log.info("UsernameAuthenticationProvider  loading......");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = (String) authentication.getPrincipal();
        UserDetails user = userService.loadUserByUsername(username);
        if (null != user) {
            return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
