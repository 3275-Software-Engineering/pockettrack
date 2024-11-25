package com.project.pockettrack.controller;

import java.sql.Date;
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
            @RequestParam(required = false) String transactionType,
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
                }else if(startDate !=null) {
                	predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
                }else if(endDate !=null) {
                	predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
                }
                if (transactionType != null) {
                	predicates.add(cb.equal(root.get("transactionType"),transactionType));
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
                }
                else if (minAmount != null ) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("transactionAmount"), minAmount));
                }
                else if (maxAmount != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("transactionAmount"), maxAmount));
                }
                

                // Combine all conditions using AND logic
                return cb.and(predicates.toArray(new Predicate[0]));
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
            
            // Set transactionDate to current date
            newTransaction.setTransactionDate(transaction.getTransactionDate());
            
            // Set other properties
            newTransaction.setTransactionType(transactionType); // Set valid transaction type
            newTransaction.setPaymentMethodName(transaction.getPaymentMethodName());
            newTransaction.setCurrency(transaction.getCurrency());
            newTransaction.setTransactionAmount(transaction.getTransactionAmount());
            newTransaction.setTransactionCategoryName(transaction.getTransactionCategoryName());
            newTransaction.setNote(transaction.getNote());
            
            // Set dateCreated and dateUpdated
            if (transaction.getDateCreated() != null) {
                newTransaction.setDateCreated(transaction.getDateCreated());
            } else {
                newTransaction.setDateCreated(new Timestamp(System.currentTimeMillis())); // Use current time
            }

            if (transaction.getDateUpdated() != null) {
                newTransaction.setDateUpdated(transaction.getDateUpdated());
            } else {
                newTransaction.setDateUpdated(new Timestamp(System.currentTimeMillis())); // Use current time
            }
            
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

            // Validate the transaction type
            TransactionType transactionType = transaction.getTransactionType();
            if (transactionType == null || !isValidTransactionType(transactionType)) {
                return new ResponseEntity<>("Invalid transaction type", HttpStatus.BAD_REQUEST);
            }

            // Update the existing Transaction object
            existingTransaction.setUser(userOptional.get());
            existingTransaction.setTransactionDate(transaction.getTransactionDate()); // Set to current date
            existingTransaction.setTransactionType(transactionType);
            existingTransaction.setPaymentMethodName(transaction.getPaymentMethodName());
            existingTransaction.setCurrency(transaction.getCurrency());
            existingTransaction.setTransactionAmount(transaction.getTransactionAmount());
            existingTransaction.setNote(transaction.getNote());
            // Set dateCreated and dateUpdated
            if (transaction.getDateCreated() != null) {
            	existingTransaction.setDateCreated(transaction.getDateCreated());
            } 
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

    // Delete all transactions
    @DeleteMapping("/transactions")
    public ResponseEntity<String> deleteAllTransactions() {
        try {
            // Delete all transactions in the repository
            transactionRepository.deleteAll();
            return new ResponseEntity<>("All transactions deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
