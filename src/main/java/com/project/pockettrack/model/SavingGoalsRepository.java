package com.project.pockettrack.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingGoalsRepository extends JpaRepository<SavingGoals, Integer>{
	
	Optional<SavingGoals> findByGoalId(int goalId);
	
	List<SavingGoals> findByUser_UserId(int userId);
	
	//根据用户ID和月份获取储蓄目标
	List<SavingGoals> findByUser_UserIdAndMonth(int userId, int month);
    
	Optional<SavingGoals> findByDeadlineDateBetween(LocalDate startDate, LocalDate endDate);
    
	Optional<SavingGoals> findByPurpose(String purpose);
    
	void deleteByUser_UserId(int userId);
	
	//根据用户ID、月份和截止日期范围获取储蓄目标
	List<SavingGoals> findByUser_UserIdAndMonthAndDeadlineDateBetween(int userId, int month, LocalDate startDate, LocalDate endDate);
	
}
