package com.neev.ToDoMaticTW.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.requests.UserRequest;
import com.neev.ToDoMaticTW.security.CustomAuthenticationManager;
import com.neev.ToDoMaticTW.security.JWTUtils;
import com.neev.ToDoMaticTW.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomAuthenticationManager customAuthenticationManager;
    @Autowired
    JWTUtils jwtUtils;

    @PostMapping("/auth/register")
    ResponseEntity registerUser(@RequestBody UserRequest userRequest){
        Map<String, String> body = new HashMap<>();

        if(isUsernameOrPasswordNull(userRequest.getUsername(), userRequest.getPassword())){
            body.put("message", "Username or password cannot be empty");
            return  ResponseEntity.badRequest().body(body);
        }

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = new User(userRequest.getUsername(), encryptedPassword);

        if(userService.doesUserExistsAlready(user.getUsername())) {
            body.put("message", "Username already taken");
            return ResponseEntity.badRequest().body(body);
        }

        userService.saveUser(user);
        body.put("message", "User Successfully Registered");
        return ResponseEntity.ok().body(body);
    }

    private boolean isUsernameOrPasswordNull(String username, String password){
        return username == null || password == null;
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody UserRequest userRequest){
        Map<String, String> body = new HashMap<>();

        if(isUsernameOrPasswordNull(userRequest.getUsername(), userRequest.getPassword())){
            body.put("message", "Username or password cannot be empty");
            return  ResponseEntity.badRequest().body(body);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword());

        try {
            Authentication authentication = customAuthenticationManager.authenticate(usernamePasswordAuthenticationToken);
            String token = jwtUtils.generateToken(authentication);

            body.put("message", "Login Successful");
            body.put("username", authentication.getName());
            body.put("access-token", token);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
        } catch (Exception e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
