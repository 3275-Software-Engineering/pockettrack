package com.project.pockettrack.model;
/*
 * Class Name: BudgetRepository.java
 * Author: Tracy
 * Date: 2024-10-13
 * Purpose: 
 */
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUserId(Integer userId);
   
    List<Budget> findByPeriodDateBetween(LocalDate startDate, LocalDate endDate);

}