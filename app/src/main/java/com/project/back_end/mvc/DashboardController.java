package com.example.javadatabasecapstone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.javadatabasecapstone.service.TokenService;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    // Admin Dashboard
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        Map<String, Object> validationResult = tokenService.validateToken(token, "admin");

        if (validationResult.isEmpty()) {
            // Token valid → load admin dashboard view
            return "admin/adminDashboard";
        } else {
            // Token invalid → redirect to login
            return "redirect:http://localhost:8080";
        }
    }

    // Doctor Dashboard
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");

        if (validationResult.isEmpty()) {
            // Token valid → load doctor dashboard view
            return "doctor/doctorDashboard";
        } else {
            // Token invalid → redirect to login
            return "redirect:http://localhost:8080";
        }
    }
}
