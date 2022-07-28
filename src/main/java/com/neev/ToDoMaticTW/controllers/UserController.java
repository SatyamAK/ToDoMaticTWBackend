package com.neev.ToDoMaticTW.controllers;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.models.UsersTask;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            body.put("message", e.getMessage());
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(body);
        }
    }

    @GetMapping("/getAllTasks")
    public ResponseEntity getAllTasks(@RequestParam String username) {
        List<UsersTask> tasksList = userService.getTasks(username);
        Map<String, List<UsersTask>> body = new HashMap<>(){
            {
                put("tasks", tasksList);
            }
        };

        return ResponseEntity.ok().body(body);
    }

    @PostMapping("/addTask")
    public ResponseEntity addNewTask(@RequestParam String username, @RequestBody UsersTask usersTask){

        List<UsersTask> taskList = userService.addTask(username, usersTask);
        Map<String, List<UsersTask>> body = new HashMap<>(){
            {
                put("updated_tasks", taskList);
            }
        };

        return ResponseEntity.ok().body(body);
    }

    @PutMapping("/updateTask")
    public ResponseEntity updateTask(@RequestParam String username, @RequestBody UsersTask usersTask){

        List<UsersTask> taskList = userService.updateTask(username, usersTask);

        Map<String, List<UsersTask>> body = new HashMap<>(){
            {
                put("updated_tasks", taskList);
            }
        };

        return ResponseEntity.ok().body(body);
    }

    @DeleteMapping("/deleteTask")
    public ResponseEntity deleteTask(@RequestParam String username, @RequestParam Integer taskId){

        List<UsersTask> taskList = userService.deleteTask(username, taskId);

        Map<String, List<UsersTask>> body = new HashMap<>(){
            {
                put("updated_tasks", taskList);
            }
        };

        return ResponseEntity.ok().body(body);
    }
}
