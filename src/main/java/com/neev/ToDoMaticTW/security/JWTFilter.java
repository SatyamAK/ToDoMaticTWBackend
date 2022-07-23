package com.neev.ToDoMaticTW.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTFilter extends BasicAuthenticationFilter {

    private JWTUtils jwtUtils;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public JWTFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException
    {
        String accessToken = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken authenticationToken = null;

        if(accessToken == null || !accessToken.startsWith("Bearer")){
            chain.doFilter(request, response);
            return;
        }

        String username = jwtUtils.getUsernameFromToken(accessToken);

        if(username!=null){
             authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, new ArrayList<GrantedAuthority>()
            );
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }
}
