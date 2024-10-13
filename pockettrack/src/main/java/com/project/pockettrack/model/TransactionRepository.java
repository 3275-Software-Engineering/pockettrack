package com.project.pockettrack.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{

	Optional<Transaction> findByTransactionId(int transactionId);
	
    List<Transaction> findByUser_UserId(int userId);
    
	Optional<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
	Optional<Transaction> findByTransactionType(TransactionType transactionType);
    
	Optional<Transaction> findByTransactionCategoryName(String transactionCategoryName);
    
	Optional<Transaction> findByCurrency(String currency); 
    
}
