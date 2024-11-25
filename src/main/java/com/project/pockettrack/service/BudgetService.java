package com.project.pockettrack.service;

/*
 * Class Name: BudgetService.java
 * Author: Tracy
 * Date: 2024-11-19
 * Purpose: 
 */

import com.project.pockettrack.model.Budget;
import com.project.pockettrack.model.BudgetRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
   @Autowired
   private BudgetRepository budgetRepository;

   public List<Budget> getAllBudgets() {
      return this.budgetRepository.findAll();
   }

   public List<Budget> getAllBudgetsByUserId(Integer userId) {
      return this.budgetRepository.findByUserId(userId);
   }

   public Budget createBudget(Budget budget, Integer userId) {
      budget.setUserId(userId);
      return (Budget)this.budgetRepository.save(budget);
   }

   public Budget updateBudget(Integer budgetId, Budget budgetDetails) {
      Optional<Budget> optionalBudget = this.budgetRepository.findById(budgetId);
      if (!optionalBudget.isPresent()) {
         return null;
      } else {
         Budget existingBudget = (Budget)optionalBudget.get();
         existingBudget.setBudgetAmount(budgetDetails.getBudgetAmount());
         existingBudget.setUserId(budgetDetails.getUserId());
         existingBudget.setPeriodType(budgetDetails.getPeriodType());
         existingBudget.setPeriodDate(budgetDetails.getPeriodDate());
         existingBudget.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
         return (Budget)this.budgetRepository.save(existingBudget);
      }
   }

   public void deleteBudget(Integer budgetId) {
      this.budgetRepository.deleteById(budgetId);
   }

   public List<Budget> getBudgetsByYearAndMonth(Integer year, Integer month) {
      LocalDate startDate = LocalDate.of(year, month, 1);
      LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
      return this.budgetRepository.findByPeriodDateBetween(startDate, endDate);
   }

   public List<Budget> getBudgetsByYear(Integer year) {
      LocalDate startDate = LocalDate.of(year, 1, 1);
      LocalDate endDate = LocalDate.of(year, 12, 31);
      return this.budgetRepository.findByPeriodDateBetween(startDate, endDate);
   }

   public Optional<Budget> getBudgetByUserAndCategoryAndPeriod(Integer userId, String categoryName, LocalDate periodDate) {
      return this.budgetRepository.findByUserIdAndCategoryNameAndPeriodDate(userId, categoryName, periodDate);
   }

   public Budget saveBudget(Budget budget) {
      return (Budget)this.budgetRepository.save(budget);
   }
}
