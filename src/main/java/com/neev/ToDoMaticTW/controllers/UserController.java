package com.neev.ToDoMaticTW.controllers;

import com.neev.ToDoMaticTW.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/auth/register")
    ResponseEntity registerUser(@RequestBody User user){

        return ResponseEntity.ok().body("User Successfully Registered");
    }
}
