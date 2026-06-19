package com.matin.applyflow.service;

import com.matin.applyflow.dto.ApplicationMapper;
import com.matin.applyflow.dto.ApplicationRequest;
import com.matin.applyflow.dto.ApplicationResponse;
import com.matin.applyflow.exception.ResourceNotFoundException;
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

    // POST — convert request to entity, save, return response DTO
    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = ApplicationMapper.toEntity(request);
        Application saved = repository.save(application);
        return ApplicationMapper.toResponse(saved);
    }

    // GET all — map each entity in the page to a response DTO
    public Page<ApplicationResponse> getApplications(ApplicationStatus status, String company, Pageable pageable) {
        Page<Application> results;

        if (status != null && company != null) {
            results = repository.findByStatusAndCompanyNameContainingIgnoreCase(status, company, pageable);
        } else if (status != null) {
            results = repository.findByStatus(status, pageable);
        } else if (company != null) {
            results = repository.findByCompanyName(company, pageable);
        } else {
            results = repository.findAll(pageable);
        }
        return results.map(ApplicationMapper::toResponse);
    }

    // PUT — fetch existing entity, update its fields from request, save, return response DTO
    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        Application application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setSalary(request.getSalary());
        application.setStatus(request.getStatus());

        Application updated = repository.save(application);
        return ApplicationMapper.toResponse(updated);
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
