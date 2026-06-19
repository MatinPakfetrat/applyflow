package com.matin.applyflow.controller;

import com.matin.applyflow.dto.ApplicationRequest;
import com.matin.applyflow.dto.ApplicationResponse;
import com.matin.applyflow.model.ApplicationStatus;
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
    public ResponseEntity<Page<ApplicationResponse>> getApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            Pageable pageable
    ) {
        return ResponseEntity.ok(applicationService.getApplications(status, company, pageable));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest a){
        ApplicationResponse saved = applicationService.createApplication(a);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponse> update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request){
        ApplicationResponse response = applicationService.updateApplication(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!applicationService.existsById(id))
            return ResponseEntity.notFound().build();
        applicationService.deleteApplicationById(id);
        return ResponseEntity.ok().build();
    }
}