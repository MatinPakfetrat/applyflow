package com.matin.applyflow.controller;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.repository.ApplicationRepository;
import com.matin.applyflow.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationRepository applicationRepo, ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(this.applicationService.getAllApplications());
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Application a){
        // Validate company name
        if(a.getCompanyName() == null || a.getCompanyName().trim().isEmpty())
            return ResponseEntity.badRequest().body("Company name cannot be blank.");

        // Validate salary
        if(a.getSalary() < 0)
            return ResponseEntity.badRequest().body("Salary cannot be negative.");

        Application saved = applicationService.saveApplication(a);
        return ResponseEntity.ok(saved);
    }
}