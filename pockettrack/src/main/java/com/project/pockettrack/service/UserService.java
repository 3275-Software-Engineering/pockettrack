package com.project.pockettrack.service;
/*
 * Class Name: UserService.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */
import com.project.pockettrack.model.User;
import com.project.pockettrack.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll(); 
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

   /* public User registerUser(User user) {
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);*/
    
    public User registerUser(User user) {
        // Validate input
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Set created and updated timestamps
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(timestamp);
        user.setUpdatedAt(timestamp);

        // Save user to the database
        return userRepository.save(user);
    }
    

    /* public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user; // Return user information
        }
        return null; // Login failed
    } */
    
    public Optional<User> loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user); // Return wrapped user in Optional
        }
        return Optional.empty(); // Return empty Optional if login fails
    }
    
    public User createUser(User user) {
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
    
    // update user info
    public Optional<User> updateUser(Integer userId, User userDetails) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // update time
            return userRepository.save(user); // save updated
        });
    }

    // delete user
    public boolean deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true; // delete sucessfully
        }
        return false; // no user found
    }
    
}