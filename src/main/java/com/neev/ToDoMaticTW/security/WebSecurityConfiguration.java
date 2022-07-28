package com.neev.ToDoMaticTW.security;

import com.neev.ToDoMaticTW.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Collections;

@Configuration
public class WebSecurityConfiguration {

    @Autowired
    UserService userService;
    @Autowired
    JWTUtils jwtUtils;

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(){
        return new EntryPointHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationManager customAuthenticationManager(){
        return new CustomAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().cors().configurationSource(corsConfigurationSource()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
                .addFilter(new JWTFilter(customAuthenticationManager(), jwtUtils))
                .authorizeRequests().antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                .and();

        return httpSecurity.build();
    }
}
