package com.project.pockettrack.model;

/*
 * Class Name: BudgetRepository.java
 * Author: Tracy
 * Date: 2024-11-19
 * Purpose: 
 */

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
   List<Budget> findByUserId(Integer userId);

   List<Budget> findByPeriodDateBetween(LocalDate startDate, LocalDate endDate);

   Optional<Budget> findByUserIdAndCategoryNameAndPeriodDate(Integer userId, String categoryName, LocalDate periodDate);
}
