package com.neev.ToDoMaticTW.controllers;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.requests.UserRequest;
import com.neev.ToDoMaticTW.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/auth/register")
    ResponseEntity registerUser(@RequestBody UserRequest userRequest){

        if(isUsernameOrPasswordNull(userRequest.getUsername(), userRequest.getPassword())){
            return  ResponseEntity.badRequest().body("Username or Password cannot be empty");
        }

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = new User(userRequest.getUsername(), encryptedPassword);

        if(userService.doesUserExistsAlready(user.getUsername()))
            return ResponseEntity.badRequest().body("Username already taken");

        userService.saveUser(user);
        return ResponseEntity.ok().body("User Successfully Registered");
    }

    private boolean isUsernameOrPasswordNull(String username, String password){
        return username == null || password == null;
    }
}
