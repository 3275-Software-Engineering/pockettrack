package com.project.pockettrack.model;
/*
 * Class Name: UserRepository.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */
import com.project.pockettrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}