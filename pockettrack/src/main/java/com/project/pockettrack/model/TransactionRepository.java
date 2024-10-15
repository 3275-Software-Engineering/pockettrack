package com.project.pockettrack.model;

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
}
