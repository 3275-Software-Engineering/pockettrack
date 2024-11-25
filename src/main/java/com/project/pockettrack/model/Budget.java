package com.project.pockettrack.model;

/*
 * Class Name: Budget.java
 * Author: Tracy
 * Date: 2024-11-19
 * Purpose: 
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(
   name = "budgets"
)
public class Budget {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Integer budgetId;
   private Integer userId;
   private String periodType;
   @JsonFormat(
      shape = Shape.STRING,
      pattern = "yyyy-MM-dd"
   )
   private LocalDate periodDate;
   private String categoryName;
   @JsonFormat(
      shape = Shape.STRING
   )
   private BigDecimal budgetAmount;
   @JsonFormat(
      shape = Shape.STRING
   )
   private BigDecimal actualExpenditure;
   @JsonFormat(
      shape = Shape.STRING
   )
   private BigDecimal variance;
   private Timestamp createdAt;
   private Timestamp updatedAt;

   public Integer getBudgetId() {
      return this.budgetId;
   }

   public void setBudgetId(Integer budgetId) {
      this.budgetId = budgetId;
   }

   public Integer getUserId() {
      return this.userId;
   }

   public void setUserId(Integer userId) {
      this.userId = userId;
   }

   public String getPeriodType() {
      return this.periodType;
   }

   public void setPeriodType(String periodType) {
      this.periodType = periodType;
   }

   public LocalDate getPeriodDate() {
      return this.periodDate;
   }

   public String getCategoryName() {
      return this.categoryName;
   }

   public void setPeriodDate(LocalDate periodDate) {
      this.periodDate = periodDate;
   }

   public BigDecimal getBudgetAmount() {
      return this.budgetAmount;
   }

   public void setBudgetAmount(BigDecimal budgetAmount) {
      this.budgetAmount = budgetAmount;
   }

   public Timestamp getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(Timestamp createdAt) {
      this.createdAt = createdAt;
   }

   public Timestamp getUpdatedAt() {
      return this.updatedAt;
   }

   public void setUpdatedAt(Timestamp updatedAt) {
      this.updatedAt = updatedAt;
   }

   public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
   }
}
