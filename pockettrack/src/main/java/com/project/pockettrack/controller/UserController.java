package com.project.pockettrack.controller;
/*
 * Class Name: UserController.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */
import com.project.pockettrack.model.User;
import com.project.pockettrack.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.status(201).body(registeredUser); // Set status to 201 Created
        
        //return ResponseEntity.ok(registeredUser);
    }
    

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> loggedInUser = userService.loginUser(username, password);
        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(loggedInUser.get()); // Return user information
        }
        return ResponseEntity.status(401).body("Login failed"); // Return error message as a String
    }
    
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Integer userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.status(404).build());
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.status(201).body(newUser);
    }
    

    // 根据用户ID更新用户信息
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(userId, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.status(404).build()); // 用户未找到
    }

    // 根据用户ID删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.noContent().build(); // 成功删除，返回204
        }
        return ResponseEntity.status(404).build(); // 用户未找到，返回404
    }
}