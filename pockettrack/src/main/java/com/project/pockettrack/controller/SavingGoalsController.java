package com.project.pockettrack.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.pockettrack.model.SavingGoals;
import com.project.pockettrack.model.SavingGoalsRepository;
import com.project.pockettrack.model.User;
import com.project.pockettrack.model.UserRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
public class SavingGoalsController {

    @Autowired
    private SavingGoalsRepository savingGoalsRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all savings goals for a specific user
    @GetMapping("/saving-goals/{userId}")
    public ResponseEntity<List<SavingGoals>> getAllSavingGoals(@PathVariable int userId) {
        try {
            List<SavingGoals> goals = savingGoalsRepository.findByUser_UserId(userId);
            return new ResponseEntity<>(goals, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create a new savings goal
    @PostMapping("/saving-goals")
    public ResponseEntity<String> createSavingGoal(@RequestBody SavingGoals savingGoal) {
        try {
            // Check if the user exists
            Optional<User> userOptional = userRepository.findById(savingGoal.getUser().getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            // Set creation and update timestamps
            savingGoal.setDateCreated(new Timestamp(System.currentTimeMillis()));
            savingGoal.setDateUpdated(new Timestamp(System.currentTimeMillis()));

            // Save the new savings goal
            savingGoalsRepository.save(savingGoal);
            return new ResponseEntity<>("Savings goal created successfully, ID: " + savingGoal.getGoalId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update a savings goal
    @PutMapping("/saving-goals/{goalId}")
    public ResponseEntity<String> updateSavingGoal(@PathVariable int goalId, @RequestBody SavingGoals savingGoal) {
        try {
            // Find the existing savings goal
            Optional<SavingGoals> existingGoalOptional = savingGoalsRepository.findById(goalId);
            if (!existingGoalOptional.isPresent()) {
                return new ResponseEntity<>("Savings goal not found", HttpStatus.NOT_FOUND);
            }

            // Update the properties of the existing savings goal
            SavingGoals existingGoal = existingGoalOptional.get();
            existingGoal.setTargetAmount(savingGoal.getTargetAmount());
            existingGoal.setDeadlinePeriod(savingGoal.getDeadlinePeriod());
            existingGoal.setDeadlineDate(savingGoal.getDeadlineDate());
            existingGoal.setPurpose(savingGoal.getPurpose());
            existingGoal.setCurrentAmount(savingGoal.getCurrentAmount());
            existingGoal.setDateUpdated(new Timestamp(System.currentTimeMillis())); // Update modified timestamp

            // Save the updated savings goal
            savingGoalsRepository.save(existingGoal);
            return new ResponseEntity<>("Savings goal updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a specific savings goal
    @DeleteMapping("/saving-goals/{goalId}")
    public ResponseEntity<String> deleteSavingGoal(@PathVariable int goalId) {
        try {
            // Check if the savings goal exists
            Optional<SavingGoals> goalOptional = savingGoalsRepository.findById(goalId);
            if (!goalOptional.isPresent()) {
                return new ResponseEntity<>("Savings goal not found", HttpStatus.NOT_FOUND);
            }

            // Delete the savings goal
            savingGoalsRepository.deleteById(goalId);
            return new ResponseEntity<>("Savings goal deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete all savings goals
    @DeleteMapping("/saving-goals")
    public ResponseEntity<String> deleteAllSavingGoals() {
        try {
            savingGoalsRepository.deleteAll();
            return new ResponseEntity<>("All savings goals deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
