package com.project.pockettrack.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingGoalsRepository extends JpaRepository<SavingGoals, Integer>{
	
	Optional<SavingGoals> findByGoalId(int goalId);
	
	List<SavingGoals> findByUser_UserId(int userId);
    
	Optional<SavingGoals> findByDeadlineDateBetween(LocalDate startDate, LocalDate endDate);
    
	Optional<SavingGoals> findByPurpose(String purpose);
    
}
