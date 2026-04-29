package com.matin.applyflow.service;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import com.matin.applyflow.repository.ApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public Application saveApplication(Application a){
        return repository.save(a);
    }

    public Page<Application> getApplications(ApplicationStatus status, String company, Pageable pageable) {
        if (status != null && company != null) {
            return repository.findByStatusAndCompanyNameContaining(status, company, pageable);
        } else if (status != null) {
            return repository.findByStatus(status, pageable);
        } else if (company != null) {
            return repository.findByCompanyName(company, pageable);
        }
        return repository.findAll(pageable);
    }
    public Optional<Application> getById(Long id){
        return repository.findById(id);
    }

    public boolean existsById(Long id){
        return repository.existsById(id);
    }

    public void deleteApplicationById(Long id){
        repository.deleteById(id);
    }

    public Page<Application> getAllApplications(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
