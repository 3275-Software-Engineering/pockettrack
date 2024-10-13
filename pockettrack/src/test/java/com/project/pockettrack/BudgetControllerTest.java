package com.project.pockettrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.pockettrack.controller.BudgetController;
import com.project.pockettrack.model.Budget;
import com.project.pockettrack.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BudgetControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

	@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(budgetController).build();
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
    }

    // Utility method to convert an object to JSON string
    private String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e); // Handle the exception properly in production code
        }
    }

    /*************************getBudgetsByUserId********************************************/
	
	
	@Test
    public void createBudget_ValidRequest_ReturnsCreated() throws Exception {
        Budget budget = new Budget();
        budget.setBudgetId(1);
        budget.setBudgetAmount(new BigDecimal("5000.0"));
        budget.setUserId(1);
        budget.setPeriodType("monthly");
        budget.setPeriodDate(LocalDate.parse("2024-01-01"));
        budget.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        budget.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(budgetService.createBudget(any(Budget.class))).thenReturn(budget);

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(budget))) 
                        //.content("{\"budgetAmount\": \"5000.0\", \"userId\": 1, \"periodType\": \"monthly\", \"periodDate\": \"2024-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json(asJsonString(budget), false));
                //.andExpect(content().json("{\"budgetId\": 1, \"budgetAmount\": \"5000.0\", \"userId\": 1, \"periodType\": \"monthly\", \"periodDate\": \"2024-01-01\"}", false));
    }

    @Test
    public void updateBudget_ValidRequest_ReturnsOk() throws Exception {
        Budget budget = new Budget();
        budget.setBudgetId(1);
        budget.setBudgetAmount(new BigDecimal("5000.0"));
        budget.setUserId(1);
        budget.setPeriodType("monthly");
        budget.setPeriodDate(LocalDate.parse("2024-01-01"));
        budget.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        when(budgetService.updateBudget(anyInt(), any(Budget.class))).thenReturn(budget);

        mockMvc.perform(put("/api/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                      //.content(asJsonString(budget)))
                        .content("{\"budgetAmount\": \"5000.0\", \"userId\": 1, \"periodType\": \"monthly\", \"periodDate\": \"2024-01-01\"}"))
                        .andExpect(status().isOk())
                      //.andExpect(content().json(asJsonString(budget), false));
                        .andExpect(content().json("{\"budgetId\": 1, \"budgetAmount\": \"5000.0\", \"userId\": 1, \"periodType\": \"monthly\", \"periodDate\": \"2024-01-01\"}", false));
    }

    @Test
    public void updateBudget_BudgetNotFound_ReturnsNotFound() throws Exception {
        when(budgetService.updateBudget(anyInt(), any(Budget.class))).thenReturn(null);

        mockMvc.perform(put("/api/budgets/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"budgetAmount\": \"5000.0\", \"userId\": 7}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteBudget_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getBudgetsByUserId_ValidUserId_ReturnsBudgets() throws Exception {
        Budget budget = new Budget();
        budget.setBudgetId(1);
        budget.setBudgetAmount(new BigDecimal("5000.0"));
        budget.setUserId(1);

        when(budgetService.getAllBudgetsByUserId(1)).thenReturn(List.of(budget));

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"budgetId\": 1, \"budgetAmount\": \"5000.0\", \"userId\": 1}]", false));
    }
}

