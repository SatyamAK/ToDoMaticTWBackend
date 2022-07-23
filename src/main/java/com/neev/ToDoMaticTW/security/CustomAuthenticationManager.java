package com.neev.ToDoMaticTW.security;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            final User user = (User) userService.loadUserByUsername(authentication.getName());

            if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
                throw new BadCredentialsException("Wrong password");
            }

            return authentication;
        }catch (UsernameNotFoundException e){
            throw new BadCredentialsException("User not found");
        }
    }
}
