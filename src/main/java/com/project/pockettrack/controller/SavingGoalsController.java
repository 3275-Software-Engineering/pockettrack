package com.project.pockettrack.controller;

import com.project.pockettrack.model.SavingGoals;
import com.project.pockettrack.model.Transaction;
import com.project.pockettrack.model.TransactionRepository;
import com.project.pockettrack.model.User;
import com.project.pockettrack.model.SavingGoalsRepository;
import com.project.pockettrack.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")


public class SavingGoalsController {

    @Autowired
    private SavingGoalsRepository savingGoalsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 获取特定用户的所有储蓄目标
     */
    @GetMapping("/saving-goals/{userId}")
    public ResponseEntity<List<SavingGoals>> getAllSavingGoals(@PathVariable int userId) {
        try {
            List<SavingGoals> goals = savingGoalsRepository.findByUser_UserId(userId);
            return new ResponseEntity<>(goals, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建新的储蓄目标
     */
    @PostMapping("/saving-goals")
    public ResponseEntity<String> createSavingGoal(@RequestBody SavingGoals savingGoal) {
        try {
            // 验证用户是否存在
            Optional<User> userOptional = userRepository.findById(savingGoal.getUser().getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            
            // Validate purpose
            List<String> validPurposes = Arrays.asList("Buy Car", "Buy House", "Travel", "Gift", "Wedding", "Retirement Savings", "Emergency", "Other");
            if (savingGoal.getPurpose() == null || !validPurposes.contains(savingGoal.getPurpose())) {
                return new ResponseEntity<>("Invalid purpose. Must be one of " + validPurposes, HttpStatus.BAD_REQUEST);
            }

            // 验证月份
            if (savingGoal.getMonth() < 1 || savingGoal.getMonth() > 12) {
                return new ResponseEntity<>("Invalid month. Must be between 1 and 12", HttpStatus.BAD_REQUEST);
            }

            // 设置用户
            savingGoal.setUser(userOptional.get());

            // 设置创建和更新时间戳
            savingGoal.setDateCreated(new Timestamp(System.currentTimeMillis()));
            savingGoal.setDateUpdated(new Timestamp(System.currentTimeMillis()));

            // 初始化当前金额
            if (savingGoal.getCurrentAmount() == null) {
                savingGoal.setCurrentAmount(BigDecimal.ZERO);
            }

            // 保存新的储蓄目标
            savingGoalsRepository.save(savingGoal);
            return new ResponseEntity<>("Saving goal created successfully, ID: " + savingGoal.getGoalId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred:  " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新储蓄目标
     */
    @PutMapping("/saving-goals/{userId}/{goalId}")
    public ResponseEntity<String> updateSavingGoal(@PathVariable int userId, @PathVariable int goalId, @Valid @RequestBody SavingGoals savingGoal) {
        try {
        	// 验证用户是否存在
            Optional<User> userOptional = userRepository.findById(savingGoal.getUser().getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            
            // 查找现有的储蓄目标
            Optional<SavingGoals> existingGoalOptional = savingGoalsRepository.findById(goalId);
            if (!existingGoalOptional.isPresent()) {
                return new ResponseEntity<>("Saving goal not found", HttpStatus.NOT_FOUND);
            }

            SavingGoals existingGoal = existingGoalOptional.get();

            // 更新月份
            if (savingGoal.getMonth() != 0) { // 0 表示未更新
                if (savingGoal.getMonth() < 1 || savingGoal.getMonth() > 12) {
                    return new ResponseEntity<>("Invalid month. Must be between 1 and 12", HttpStatus.BAD_REQUEST);
                }
                existingGoal.setMonth(savingGoal.getMonth());
            }

            // 更新目标金额
            if (savingGoal.getTargetAmount() != null && savingGoal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                existingGoal.setTargetAmount(savingGoal.getTargetAmount());
            }

            // 更新截止日期
            if (savingGoal.getDeadlineDate() != null) {
                existingGoal.setDeadlineDate(savingGoal.getDeadlineDate());
            }

            // update the DeadlinePeriod, added by Lenny
            existingGoal.setDeadlinePeriod(savingGoal.getDeadlinePeriod());

            // 更新目的
            if (savingGoal.getPurpose() != null && !savingGoal.getPurpose().isBlank()) {
                List<String> validPurposes = Arrays.asList("Buy Car", "Buy House", "Travel", "Gift", "Wedding", "Retirement Savings", "Emergency", "Other");
                if (!validPurposes.contains(savingGoal.getPurpose())) {
                    return new ResponseEntity<>("Invalid purpose. Must be one of " + validPurposes, HttpStatus.BAD_REQUEST);
                }
                existingGoal.setPurpose(savingGoal.getPurpose());
            }

            // 更新当前金额（如果需要）
            if (savingGoal.getCurrentAmount() != null && savingGoal.getCurrentAmount().compareTo(BigDecimal.ZERO) >= 0) {
                existingGoal.setCurrentAmount(savingGoal.getCurrentAmount());
            }

            // 更新修改时间
            existingGoal.setDateUpdated(new Timestamp(System.currentTimeMillis()));

            // 保存更新后的储蓄目标
            savingGoalsRepository.save(existingGoal);
            return new ResponseEntity<>("Saving goal updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除特定的储蓄目标
     */
    @DeleteMapping("/saving-goals/{userId}/{goalId}")
    public ResponseEntity<String> deleteSavingGoal(@PathVariable int userId,@PathVariable int goalId) {
        try {
            // 检查储蓄目标是否存在
            Optional<SavingGoals> goalOptional = savingGoalsRepository.findById(goalId);
            if (!goalOptional.isPresent()) {
                return new ResponseEntity<>("Saving goal not found", HttpStatus.NOT_FOUND);
            }

            // 删除储蓄目标
            savingGoalsRepository.deleteById(goalId);
            return new ResponseEntity<>("Saving goal deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除所有储蓄目标
     */
    @DeleteMapping("/{userId}/saving-goals")
    public ResponseEntity<String> deleteAllSavingGoals(@PathVariable int userId) {
        try {
            // savingGoalsRepository.deleteAll();
            // Check if the user exists
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Delete all goalId for the given user
            savingGoalsRepository.deleteByUser_UserId(userId);
            
            return new ResponseEntity<>("All saving goals deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取储蓄目标报告
     */
    @GetMapping("/saving-goals/report/{userId}")
    public ResponseEntity<Map<String, Object>> getSavingGoalsReport(
            @PathVariable int userId,
            @RequestParam String period, // "monthly"
            @RequestParam(required = false) Integer month){ // 必填1-12
             

        
        // 验证周期
        if (period == null || !period.equalsIgnoreCase("monthly")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid period. Must be 'monthly'."));
        }

        // 验证月份
        if (month == null || month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid month. Must be between 1 and 12"));
        }

        try {
            // 获取用户
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();

            // 获取储蓄目标
            List<SavingGoals> savingGoals = savingGoalsRepository.findByUser_UserIdAndMonth(userId, month);

            // 准备报告
            List<Map<String, Object>> reportList = new ArrayList<>();

            for (SavingGoals goal : savingGoals) {
                Map<String, Object> goalReport = new LinkedHashMap<>();
                goalReport.put("dateAdded", goal.getDateCreated());

                goalReport.put("period", "Month " + goal.getMonth());
                goalReport.put("targetAmount", goal.getTargetAmount());

                // 计算当前存款
                // 假设截止日期为当月最后一天
                YearMonth yearMonth = YearMonth.of(goal.getDeadlineDate().getYear(), goal.getMonth());
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                List<Transaction> transactions = transactionRepository.findByUser_UserIdAndTransactionDateBetween(userId, startDate, endDate);

                BigDecimal totalIncome = transactions.stream()
                	    .filter(t -> t.getTransactionType() == com.project.pockettrack.model.TransactionType.income) 
                	    .map(Transaction::getTransactionAmount)
                	    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalExpense = transactions.stream()
                	    .filter(t -> t.getTransactionType() == com.project.pockettrack.model.TransactionType.expense)
                	    .map(Transaction::getTransactionAmount)
                	    .reduce(BigDecimal.ZERO, BigDecimal::add);


                BigDecimal currentMoney = totalIncome.subtract(totalExpense);
                goalReport.put("currentmoney", currentMoney);

                // 计算剩余金额
                BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentMoney);
                if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    goalReport.put("status", "Goal achieved!!!");
                } else {
                    goalReport.put("remainingAmount", remainingAmount);
                }

                reportList.add(goalReport);
            }

            // 准备最终报告
            Map<String, Object> finalReport = new LinkedHashMap<>();
            finalReport.put("userId", userId);
            finalReport.put("period", period);
            finalReport.put("month", month);
            finalReport.put("savingsGoals", reportList);

            return new ResponseEntity<>(finalReport, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error occurs: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

