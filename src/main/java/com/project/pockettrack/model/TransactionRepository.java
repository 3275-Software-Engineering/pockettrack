package com.project.pockettrack.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction>{

	Optional<Transaction> findByTransactionId(int transactionId);
	
    List<Transaction> findByUser_UserId(int userId);
    
    void deleteByUser_UserId(int userId);

	Optional<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
	Optional<Transaction> findByTransactionType(TransactionType transactionType);
    
	Optional<Transaction> findByTransactionCategoryName(String transactionCategoryName);
    
	Optional<Transaction> findByCurrency(String currency); 
	
	List<Transaction> findByUser_UserIdAndTransactionDateBetween(int userId, LocalDate startDate, LocalDate endDate);
	
}
