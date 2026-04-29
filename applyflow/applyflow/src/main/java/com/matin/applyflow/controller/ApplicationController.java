package com.matin.applyflow.controller;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import com.matin.applyflow.repository.ApplicationRepository;
import com.matin.applyflow.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<Application>> getApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            Pageable pageable
    ) {
        return ResponseEntity.ok(applicationService.getApplications(status, company, pageable));
    }

    @PostMapping("/applications")
    public ResponseEntity<Application> apply(@Valid @RequestBody Application a){
        Application saved = applicationService.saveApplication(a);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/applications/{id}")
    public ResponseEntity<Application> update(@PathVariable Long id, @Valid @RequestBody Application updated){
        return applicationService.getById(id).map(existing -> {
            existing.setCompanyName(updated.getCompanyName());
            existing.setJobTitle(updated.getJobTitle());
            if(updated.getSalary() != null)
                existing.setSalary(updated.getSalary());
            existing.setStatus(updated.getStatus());

            applicationService.saveApplication(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!applicationService.existsById(id))
            return ResponseEntity.notFound().build();
        applicationService.deleteApplicationById(id);
        return ResponseEntity.ok().build();
    }
}