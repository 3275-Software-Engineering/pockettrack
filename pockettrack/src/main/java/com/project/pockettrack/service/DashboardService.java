package com.project.pockettrack.service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.pockettrack.model.Budget;
import com.project.pockettrack.model.BudgetRepository;
import com.project.pockettrack.model.Dashboard;
import com.project.pockettrack.model.SavingGoals;
import com.project.pockettrack.model.SavingGoalsRepository;
import com.project.pockettrack.model.Transaction;
import com.project.pockettrack.model.TransactionRepository;
import com.project.pockettrack.model.TransactionType;

@Service
public class DashboardService {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private BudgetRepository budgetRepository;
	@Autowired
	private SavingGoalsRepository savingGoalsRepository;

	public Dashboard getDashboardData(int userId) {
		Dashboard dashboard = new Dashboard();

		// 1. Get balance,total income and total expense
		List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
		double totalIncome = transactions.stream().filter(t -> t.getTransactionType() == TransactionType.income)
				.mapToDouble(t -> t.getTransactionAmount().doubleValue())
				.sum();
		double totalExpense = transactions.stream().filter(t -> t.getTransactionType() == TransactionType.expense)
				 .mapToDouble(t -> t.getTransactionAmount().doubleValue())
				 .sum();
		dashboard.setBalance(totalIncome-totalExpense);
		dashboard.setTotalIncome(totalIncome);
		dashboard.setTotalExpense(totalExpense);

		// 2. Use stream to sum up all the total saving goal
		List<SavingGoals> savingGoalsList = savingGoalsRepository.findByUser_UserId(userId);
		// Calculate the total saving goal amount
		double totalSavingGoal = savingGoalsList.stream()
		    .mapToDouble(goal -> goal.getTargetAmount().doubleValue())  // Convert BigDecimal to double
		    .sum();  // Sum all target amounts

		// Set the total saving goal in the dashboard
		dashboard.setSavingGoal(totalSavingGoal);
		

		List<Budget> budgetList = budgetRepository.findByUserId(userId);
		// 3. Use stream to sum up all totalBudget values
		double totalBudget = budgetList.stream()
		    .mapToDouble(b -> b.getBudgetAmount().doubleValue())  // Convert BigDecimal to double
		    .sum();  // Sum up all totalBudget values

		// Set the accumulated result to the dashboard
		dashboard.setBudgetGoal(totalBudget);

		
        // 4. Group expenses and income by category
		Map<String, Double> incomeByCategory = transactions.stream()
			    .filter(t -> t.getTransactionType() == TransactionType.income)
			    .collect(Collectors.groupingBy(
			        Transaction::getTransactionCategoryName,
			        Collectors.summingDouble(t -> t.getTransactionAmount().doubleValue())
			    ));

        Map<String, Double> expenseByCategory = transactions.stream()
            .filter(t -> t.getTransactionType() == TransactionType.expense)
            .collect(Collectors.groupingBy(
                Transaction::getTransactionCategoryName,
                Collectors.summingDouble(t -> t.getTransactionAmount().doubleValue())
            ));

		dashboard.setIncomeByCategory(incomeByCategory);
		dashboard.setExpenseByCategory(expenseByCategory);
		
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 5. Group income by year and month
        Map<String, Double> incomeByMonth = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.income)
                .collect(Collectors.groupingBy(
                    transaction -> transaction.getTransactionDate().format(dateFormatter), // Format LocalDate
                    Collectors.summingDouble(t -> t.getTransactionAmount().doubleValue())
                ));
        
        // 6. Sort incomeByMonth by the month key and collect into a LinkedHashMap
        Map<String, Double> sortedIncomeByMonth = incomeByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by the formatted date key (yyyyMM)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new // Preserve the order
                ));
        
        // Group expenses by year and month
        Map<String, Double> expenseByMonth = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.expense)
                .collect(Collectors.groupingBy(
                    transaction -> transaction.getTransactionDate().format(dateFormatter), // Use the transaction date
                    Collectors.summingDouble(t -> t.getTransactionAmount().doubleValue())
                ));
        
       // Sort expenseByMonth by the month key and collect into a LinkedHashMap
        Map<String, Double> sortedExpenseByMonth = expenseByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by the formatted date key (yyyyMM)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new // Preserve the order
                ));
        
        // Set the results to the dashboard
        dashboard.setIncomeByMonth(sortedIncomeByMonth);
        dashboard.setExpenseByMonth(sortedExpenseByMonth);

		return dashboard;
	}
	   // Helper method to extract formatted date
       private String formatDate(Date date) {
        LocalDateTime dateTime = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMM")); // Format to yyyyMM
    }
}
