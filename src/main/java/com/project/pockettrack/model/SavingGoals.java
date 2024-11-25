package com.project.pockettrack.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "savings_goals")
public class SavingGoals {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int goalId;

	// A transaction is only associated with a User object
	// Using FetchType.EAGER will not significantly harm the efficiency
	@ManyToOne(fetch = FetchType.EAGER, optional = false) //Multiple SavingGoals can be associated with one User
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "month")
    @NotNull(message = "month cant be empty")
    private int month; // 1-12
	
	//目標金額 (targetAmount)
	@Column(name = "target_amount", precision =15,scale = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@NotNull(message = "目标金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "targetAmount must greater than 0")
	private BigDecimal targetAmount;
	
	//截止期限期間 (deadlinePeriod)
	@Column(name = "deadline_period")
	private String deadlinePeriod;

	//截止日期 (deadlineDate)
	@Column(name = "deadline_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadlineDate;
	
	//目的 (purpose)
	@Column(name = "purpose")
	private String purpose;
	
	//當前金額 (currentAmount)
	@Column(name = "current_amount", precision =15,scale = 2)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal currentAmount;

	//創建與更新時間 (dateCreated 和 dateUpdated)
	@Column(name = "created_at")
	private Timestamp dateCreated;
	
	@Column(name = "updated_at")
	private Timestamp dateUpdated;
	
	
	
	public SavingGoals() {

	}

	public SavingGoals(int goalId) {
		this.goalId = goalId;
	}
	
	//Getter and Setter Method

	public int getGoalId() {
		return goalId;
	}

	public void setGoalId(int goalId) {
		this.goalId = goalId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
    
	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public String getDeadlinePeriod() {
		return deadlinePeriod;
	}

	public void setDeadlinePeriod(String deadlinePeriod) {
		this.deadlinePeriod = deadlinePeriod;
	}

	public LocalDate getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(LocalDate deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
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
		return "SavingGoals [goalId=" + goalId + ", user=" + user + ", targetAmount=" + targetAmount
				+ ", deadlinePeriod=" + deadlinePeriod + ", deadlineDate=" + deadlineDate + ", purpose=" + purpose
				+ ", currentAmount=" + currentAmount + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated
				+ "]";
	}

}
