package com.matin.applyflow.service;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public Application saveApplication(Application a){
        return repository.save(a);
    }

    public List<Application> getAllApplications(){
        return repository.findAll();
    }
}
