package com.project.pockettrack.controller;
/*
 * Class Name: BudgetController.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */
import com.project.pockettrack.model.Budget;
import com.project.pockettrack.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> allBudgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(allBudgets);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Integer userId) {
        List<Budget> budgets = budgetService.getAllBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping
    public ResponseEntity<Budget> createBudget(@Validated @RequestBody Budget budget) {
        Budget newBudget = budgetService.createBudget(budget);
        return new ResponseEntity<>(newBudget, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Integer budgetId, @RequestBody Budget budgetDetails) {
        Budget updatedBudget = budgetService.updateBudget(budgetId, budgetDetails);
        
        if (updatedBudget == null) {
            return ResponseEntity.notFound().build(); // Return 404 if budget not found
        }
        
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byDate")
    public ResponseEntity<List<Budget>> getBudgetsByYearAndMonth(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        List<Budget> budgets = budgetService.getBudgetsByYearAndMonth(year, month);
        return ResponseEntity.ok(budgets);
    }
   
    @GetMapping("/byYear")
    public ResponseEntity<List<Budget>> getBudgetsByYear(@RequestParam Integer year) {
        List<Budget> budgets = budgetService.getBudgetsByYear(year);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/report/{userId}/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getBudgetReport(
            @PathVariable Integer userId,
            @PathVariable Integer year,
            @PathVariable Integer month) {

        // Validate input parameters
        if (userId < 0 || year < 2000 || month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters"));
        }

        // Overall report initialization
        Map<String, Object> report = new LinkedHashMap<>(); // Maintain order
        Map<String, Double> categoryTotals = new LinkedHashMap<>(); // Maintain order

        // Create periodDate for the specified year and month
        LocalDate periodDate = LocalDate.of(year, month, 1);
        List<Budget> budgets = budgetService.getAllBudgetsByUserId(userId); // Fetch budgets for user

        // Convert periodDate to string in "YYYY-MM" format
        String periodDateStr = periodDate.toString().substring(0, 7); // "YYYY-MM"

        // Calculate total budget for each category
        for (Budget budget : budgets) {
            if (budget.getPeriodDate().toString().substring(0, 7).equals(periodDateStr)) { // Compare with string format
                String categoryName = budget.getCategoryName();
                Double budgetAmount = budget.getBudgetAmount().doubleValue();

                // Initialize totalBudget and totalSpent in categoryTotals
                categoryTotals.put(categoryName + "totalBudget", budgetAmount);
                categoryTotals.put(categoryName + "totalSpent", 0.0); // Initialize totalSpent
                // Initialize variance to 0.0 to calculate later
                categoryTotals.put(categoryName + "totalvariance", 0.0); 
            }
        }

        // Get start and end dates for the month
        LocalDate startDate = periodDate.withDayOfMonth(1);
        LocalDate endDate = periodDate.withDayOfMonth(periodDate.lengthOfMonth()); // Last day of the month

        // Prepare for actual expenditure retrieval
        String jdbcUrl = "jdbc:mysql://mysql.sqlpub.com/pocket_track3275"; // Your database URL
        String dbUser = "pocket_track3275"; // Your database username
        String dbPassword = "5L9vtWnHubNhpWuA"; // Your database password

        // SQL query to retrieve actual spending by category
        String query = String.format(
            "SELECT transaction_category_name, SUM(transaction_amount) AS totalSpent " +
            "FROM transactions " +
            "WHERE user_id = %d AND transaction_type = 'expense' AND date_created BETWEEN '%s' AND '%s' " +
            "GROUP BY transaction_category_name",
            userId, startDate.toString(), endDate.toString());

        // Execute the query and retrieve results
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String categoryName = resultSet.getString("transaction_category_name");
                BigDecimal amount = resultSet.getBigDecimal("totalSpent"); // Get BigDecimal
                double totalSpentAmount = amount != null ? amount.doubleValue() : 0.0; // Convert to double

                // Update the corresponding category's totalSpent
                categoryTotals.put(categoryName + "totalSpent", totalSpentAmount); // Update totalSpent

                // Update the corresponding variance
                double budgetAmount = categoryTotals.getOrDefault(categoryName + "totalBudget", 0.0);
                double variance = budgetAmount - totalSpentAmount;
                categoryTotals.put(categoryName + "totalvariance", variance);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
            return ResponseEntity.status(500).body(Map.of("error", "Database error"));
        }

        // Calculate overall totals
        double totalBudget = 0.0;
        double totalSpent = 0.0;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String categoryName = entry.getKey();
            if (categoryName.endsWith("totalBudget")) {
                totalBudget += entry.getValue();
                // Add totalSpent for overall calculation
                totalSpent += categoryTotals.getOrDefault(categoryName.replace("totalBudget", "totalSpent"), 0.0);
            }
        }

        // Calculate overall variance
        double overallVariance = totalBudget - totalSpent;

        // Prepare the overall report with the results in the specified order
        report.put("totalBudget", totalBudget);
        report.put("totalSpent", totalSpent);
        report.put("variance", overallVariance);
        report.put("categoryTotals", categoryTotals); // Return spending by category

        return ResponseEntity.ok(report);
    }
}