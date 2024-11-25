package com.project.pockettrack.model;

import java.util.Map;

public class Dashboard {
	private double balance;         // Account balance
	private double totalIncome;     // Total Income
    private double totalExpense;    // Total Expense
    private double savingGoal;      // Saving Goal
    private double budgetGoal;      // Budget Goal
    private Map<String, Double> incomeByCategory;  // Income by category
    private Map<String, Double> expenseByCategory; // Expense by category
    private Map<String, Double> incomeByMonth; // Income by month(yyyyMM)
    private Map<String, Double> expenseByMonth; // Expense by month(yyyyMM)
    
    public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}
	public double getTotalExpense() {
		return totalExpense;
	}
	public void setTotalExpense(double totalExpense) {
		this.totalExpense = totalExpense;
	}
	public double getSavingGoal() {
		return savingGoal;
	}
	public void setSavingGoal(double savingGoal) {
		this.savingGoal = savingGoal;
	}
	public double getBudgetGoal() {
		return budgetGoal;
	}
	public void setBudgetGoal(double budgetGoal) {
		this.budgetGoal = budgetGoal;
	}
	public Map<String, Double> getIncomeByCategory() {
		return incomeByCategory;
	}
	public void setIncomeByCategory(Map<String, Double> incomeByCategory) {
		this.incomeByCategory = incomeByCategory;
	}
	public Map<String, Double> getExpenseByCategory() {
		return expenseByCategory;
	}
	public void setExpenseByCategory(Map<String, Double> expenseByCategory) {
		this.expenseByCategory = expenseByCategory;
	}
	
	public Map<String, Double> getIncomeByMonth() {
		return incomeByMonth;
	}
	public void setIncomeByMonth(Map<String, Double> incomeByMonth) {
		this.incomeByMonth = incomeByMonth;
	}
	public Map<String, Double> getExpenseByMonth() {
		return expenseByMonth;
	}
	public void setExpenseByMonth(Map<String, Double> expenseByMonth) {
		this.expenseByMonth = expenseByMonth;
	}
	@Override
	public String toString() {
		return "Dashboard [balance=" + balance + ", totalIncome=" + totalIncome + ", totalExpense=" + totalExpense
				+ ", savingGoal=" + savingGoal + ", budgetGoal=" + budgetGoal + ", incomeByCategory=" + incomeByCategory
				+ ", expenseByCategory=" + expenseByCategory + "]";
	}

    
}
