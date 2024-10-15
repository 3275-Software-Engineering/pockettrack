package com.project.pockettrack.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import com.project.pockettrack.model.Transaction;
import com.project.pockettrack.model.TransactionRepository;
import com.project.pockettrack.model.TransactionType;
import com.project.pockettrack.model.User;
import com.project.pockettrack.model.UserRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;


@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TransactionController {
	@Autowired
	TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    
	// To get various conditions transactions
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @PathVariable int userId,  // userId is required
            @RequestParam(required = false, defaultValue = "0") int transactionId, // default to 0 if not provided
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String transactionCategoryName,
            @RequestParam(required = false) String paymentMethodName,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {

        try {
            List<Transaction> transactions = new ArrayList<>();

            // Build query conditions based on the provided filters, including the required userId
            transactions = transactionRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // userId is always required
                predicates.add(cb.equal(root.get("user").get("userId"), userId)); // Assuming you need to use userId from User entity

                // Add filters for other conditions
                if (transactionId > 0) { // Only add condition if transactionId is greater than 0
                    predicates.add(cb.equal(root.get("transactionId"), transactionId));
                }

                if (startDate != null && endDate != null) {
                    predicates.add(cb.between(root.get("transactionDate"), startDate, endDate));
                }else if(startDate != null) {
                    // If only startDate is provided, filter by startDate and beyond
                    predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
                }else if(endDate != null) {
                	// If only endDate is provided, filter by endDate and before
                    predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
                }

                if (transactionCategoryName != null) {
                    predicates.add(cb.like(cb.lower(root.get("transactionCategoryName")),
                            "%" + transactionCategoryName.toLowerCase() + "%"));
                }

                if (paymentMethodName != null) {
                    predicates.add(cb.like(cb.lower(root.get("paymentMethodName")),
                            "%" + paymentMethodName.toLowerCase() + "%"));
                }

                if (currency != null) {
                    predicates.add(cb.equal(root.get("currency"), currency));
                }

                if (minAmount != null && maxAmount != null) {
                    predicates.add(cb.between(root.get("transactionAmount"), minAmount, maxAmount));
                }else if(minAmount != null) {
                	// If only minAmount is provided, filter by minAmount and beyond
                    predicates.add(cb.greaterThanOrEqualTo(root.get("transactionAmount"), minAmount));
                }else if(maxAmount != null){
                	// If only maxAmount is provided, filter by maxAmount and before
                    predicates.add(cb.lessThanOrEqualTo(root.get("transactionAmount"), maxAmount));
                }

                // Combine all conditions using AND logic
                query.where(cb.and(predicates.toArray(new Predicate[0])));

                // Add ORDER BY transactionId
                query.orderBy(cb.asc(root.get("transactionId"))); // Ascending order by transactionId

                return query.getRestriction();
            });

            return new ResponseEntity<>(transactions, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/transactions")
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        try {
            // Check if the user exists
            Optional<User> userOptional = userRepository.findById(transaction.getUser().getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            // Validate the transaction type
            TransactionType transactionType = transaction.getTransactionType();
            if (transactionType == null || !isValidTransactionType(transactionType)) {
                return new ResponseEntity<>("Invalid transaction type", HttpStatus.BAD_REQUEST);
            }

            // Create a new Transaction object
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(userOptional.get());  // Set the associated User object
            
           // Check if transactionDate is provided, if not, set to current date
            if (transaction.getTransactionDate() != null) {
                newTransaction.setTransactionDate(transaction.getTransactionDate());
            } else {
                newTransaction.setTransactionDate(LocalDate.now()); // Set to current date if no value is provided
            }
            
            // Set other properties
            newTransaction.setTransactionType(transactionType); // Set valid transaction type
            newTransaction.setPaymentMethodName(transaction.getPaymentMethodName());
            newTransaction.setTransactionCategoryName(transaction.getTransactionCategoryName());
            newTransaction.setCurrency(transaction.getCurrency());
            // Set transaction amount, default to 0.0 if not provided
            if (transaction.getTransactionAmount() != null) {
                newTransaction.setTransactionAmount(transaction.getTransactionAmount());
            } else {
                newTransaction.setTransactionAmount(BigDecimal.ZERO); // Default to 0.0 if no value is provided
            }
            newTransaction.setNote(transaction.getNote());
            
            newTransaction.setDateCreated(new Timestamp(System.currentTimeMillis())); // Use current time
            
            // Save the new Transaction
            Transaction savedTransaction = transactionRepository.save(newTransaction);
            
            return new ResponseEntity<>("Transaction created successfully: " + savedTransaction.getTransactionId(), HttpStatus.CREATED);
        
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to validate transaction type
    private boolean isValidTransactionType(TransactionType transactionType) {
    	return transactionType == TransactionType.expense || transactionType == TransactionType.income;
    }
	
    // Update a transaction record
    @PutMapping("/transactions/{transactionId}")
    public ResponseEntity<String> updateTransaction(
            @PathVariable int transactionId,
            @RequestBody Transaction transaction) {
        try {
            // Find the existing Transaction
            Optional<Transaction> existingTransactionOptional = transactionRepository.findById(transactionId);
            if (!existingTransactionOptional.isPresent()) {
                return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
            }

            // Get the existing Transaction object
            Transaction existingTransaction = existingTransactionOptional.get();

            // Check if the user exists
            Optional<User> userOptional = userRepository.findById(transaction.getUser().getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
            
            // Check if the user ID is being modified
            if (existingTransaction.getUser().getUserId() != transaction.getUser().getUserId()) {
                return new ResponseEntity<>("User ID cannot be modified", HttpStatus.BAD_REQUEST);
            }

            // Validate the transaction type
            TransactionType transactionType = transaction.getTransactionType();
            if (transactionType == null || !isValidTransactionType(transactionType)) {
                return new ResponseEntity<>("Invalid transaction type", HttpStatus.BAD_REQUEST);
            }

            // Update the existing Transaction object
            existingTransaction.setUser(userOptional.get());
         // Check if transactionDate is provided, if not, set to current date
            if (transaction.getTransactionDate() != null) {
            	existingTransaction.setTransactionDate(transaction.getTransactionDate());
            } else {
            	existingTransaction.setTransactionDate(LocalDate.now()); // Set to current date if no value is provided
            }
            existingTransaction.setTransactionType(transactionType);
            existingTransaction.setPaymentMethodName(transaction.getPaymentMethodName());
            existingTransaction.setTransactionCategoryName(transaction.getTransactionCategoryName());
            existingTransaction.setCurrency(transaction.getCurrency());
            existingTransaction.setTransactionAmount(transaction.getTransactionAmount());
            existingTransaction.setNote(transaction.getNote());
            existingTransaction.setDateUpdated(new Timestamp(System.currentTimeMillis())); // Use current time
            
            // Save the updated Transaction
            transactionRepository.save(existingTransaction);

            return new ResponseEntity<>("Transaction updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
    // Delete a single transaction by its ID
    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable int transactionId) {
        try {
            // Check if the transaction exists
            Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
            if (!transactionOptional.isPresent()) {
                return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
            }

            // Delete the transaction
            transactionRepository.deleteById(transactionId);
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete all transactions for a specific user
    @DeleteMapping("/users/{userId}/transactions")
    @Transactional  
    public ResponseEntity<String> deleteAllTransactions(@PathVariable int userId) {
        try {
            // Check if the user exists
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            // Delete all transactions for the given user
            transactionRepository.deleteByUser_UserId(userId);

            return new ResponseEntity<>("All transactions for user ID " + userId + " deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
