package com.project.pockettrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.pockettrack.model.Dashboard;
import com.project.pockettrack.service.DashboardService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Dashboard> getDashboardData(@PathVariable int userId) {
        Dashboard dashboardData = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }
}
