package com.project.pockettrack.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
@Entity
@Table(name = "transactions")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int transactionId;

	// A transaction is only associated with a User object
	// Using FetchType.EAGER will not significantly harm the efficiency
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "transaction_date")
	private LocalDate transactionDate;
	
	@Column(name = "transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Column(name = "transaction_category_name")
	private String transactionCategoryName;
	
	@Column(name = "payment_method_name")
	private String paymentMethodName;
	
	@Column(name = "currency")
	private String currency;
	
	@Column(name = "transaction_amount", precision =10,scale = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal transactionAmount;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "date_created")
	private Timestamp dateCreated;
	
	@Column(name = "date_updated")
	private Timestamp dateUpdated;
	
	public Transaction() {

	}

	public Transaction(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionCategoryName() {
		return transactionCategoryName;
	}

	public void setTransactionCategoryName(String transactionCategoryName) {
		this.transactionCategoryName = transactionCategoryName;
	}

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Timestamp getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", user=" + user + ", transactionDate=" + transactionDate
				+ ", transactionType=" + transactionType + ", transactionCategoryName=" + transactionCategoryName
				+ ", paymentMethodName=" + paymentMethodName + ", currency=" + currency + ", transactionAmount="
				+ transactionAmount + ", note=" + note + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated
				+ "]";
	}


	
}
