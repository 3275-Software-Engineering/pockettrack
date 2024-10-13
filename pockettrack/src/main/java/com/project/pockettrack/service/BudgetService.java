package com.project.pockettrack.service;

import com.project.pockettrack.model.Budget;
import com.project.pockettrack.model.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<Budget> getAllBudgetsByUserId(Integer userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget createBudget(Budget budget) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        budget.setCreatedAt(now);
        budget.setUpdatedAt(now);
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Integer budgetId, Budget budgetDetails) {
        Optional<Budget> optionalBudget = budgetRepository.findById(budgetId);
        if (!optionalBudget.isPresent()) {
            return null; // Return null if budget not found
        }

        Budget existingBudget = optionalBudget.get();
        existingBudget.setBudgetAmount(budgetDetails.getBudgetAmount());
        existingBudget.setUserId(budgetDetails.getUserId());
        existingBudget.setPeriodType(budgetDetails.getPeriodType());
        existingBudget.setPeriodDate(budgetDetails.getPeriodDate());
        existingBudget.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Update timestamp
        
        return budgetRepository.save(existingBudget);

    }
    
    public void deleteBudget(Integer budgetId) {
        budgetRepository.deleteById(budgetId);
    }
    

    public List<Budget> getBudgetsByYearAndMonth(Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // Last day of the month
        return budgetRepository.findByPeriodDateBetween(startDate, endDate);
    }
    
    public List<Budget> getBudgetsByYear(Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1); // 年的开始日期
        LocalDate endDate = LocalDate.of(year, 12, 31); // 年的结束日期
        return budgetRepository.findByPeriodDateBetween(startDate, endDate);
    }
    

}
