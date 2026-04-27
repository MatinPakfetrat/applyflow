package com.matin.applyflow.controller;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.repository.ApplicationRepository;
import com.matin.applyflow.service.ApplicationService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Application>> getAll() {
        return ResponseEntity.ok(this.applicationService.getAllApplications());
    }

    @PostMapping("/applications")
    public ResponseEntity<Application> apply(@Valid @RequestBody Application a){
        Application saved = applicationService.saveApplication(a);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/applications/{id}")
    public ResponseEntity<Application> update(@PathVariable Long id, @Valid @RequestBody Application updated){
        return applicationService.getApplicationById(id).map(existing -> {
            existing.setCompanyName(updated.getCompanyName());
            existing.setJobTitle(updated.getJobTitle());
            if(updated.getSalary() != null)
                existing.setSalary(updated.getSalary());

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