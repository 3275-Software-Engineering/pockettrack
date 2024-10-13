package com.project.pockettrack;
/*
 * Class Name: UserControllerTest.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */

import com.project.pockettrack.controller.UserController;
import com.project.pockettrack.model.User;
import com.project.pockettrack.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testRegisterUser() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.register(user);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).registerUser(any(User.class));
    }

    @SuppressWarnings("deprecation")
	@Test
    public void testLoginUser_Success() {
        // Mocking the loginUser method to return an Optional containing the user
        when(userService.loginUser("testUser", "password123")).thenReturn(Optional.of(user));

        // Performing the login action
        ResponseEntity<?> response = userController.login("testUser", "password123");

        // Verifying the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).loginUser("testUser", "password123");
    }

    @SuppressWarnings("deprecation")
	@Test
    public void testLoginUser_Failure() {
        // Mocking the loginUser method to return an empty Optional
        when(userService.loginUser("testUser", "wrongPassword")).thenReturn(Optional.empty());

        // Performing the login action
        ResponseEntity<?> response = userController.login("testUser", "wrongPassword");

        // Verifying the response
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Login failed", response.getBody());
        verify(userService, times(1)).loginUser("testUser", "wrongPassword");
    }
    

}
