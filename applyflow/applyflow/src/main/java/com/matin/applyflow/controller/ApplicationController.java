package com.matin.applyflow.controller;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.repository.ApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationRepository applicationRepo;

    public ApplicationController(ApplicationRepository applicationRepo) {
        this.applicationRepo = applicationRepo;
    }

    @GetMapping("/")
    public String test() {
        return "ApplyFlow running";
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Application a){
        Application saved = applicationRepo.save(a);
        return ResponseEntity.ok(saved);
    }
}