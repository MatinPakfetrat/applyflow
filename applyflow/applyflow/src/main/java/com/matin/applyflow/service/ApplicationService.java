package com.matin.applyflow.service;

import com.matin.applyflow.dto.ApplicationMapper;
import com.matin.applyflow.dto.ApplicationRequest;
import com.matin.applyflow.dto.ApplicationResponse;
import com.matin.applyflow.exception.ResourceNotFoundException;
import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import com.matin.applyflow.model.User;
import com.matin.applyflow.repository.ApplicationRepository;
import com.matin.applyflow.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    // POST — convert request to entity, save, return response DTO
    public ApplicationResponse createApplication(ApplicationRequest request) {
        User user = getCurrentUser();
        Application application = ApplicationMapper.toEntity(request);
        application.setUser(user);
        return ApplicationMapper.toResponse(applicationRepository.save(application));
    }

    // GET all — map each entity in the page to a response DTO
    public Page<ApplicationResponse> getApplications(ApplicationStatus status, String company, Pageable pageable) {
        User user = getCurrentUser();

        if (status != null && company != null) {
            return applicationRepository.findByUserAndStatusAndCompanyNameIgnoreCase(user, status, company, pageable)
                    .map(ApplicationMapper::toResponse);
        } else if (status != null) {
            return applicationRepository.findByUserAndStatus(user, status, pageable)
                    .map(ApplicationMapper::toResponse);
        } else if (company != null) {
            return applicationRepository.findByUserAndCompanyNameIgnoreCase(user, company, pageable)
                    .map(ApplicationMapper::toResponse);
        } else {
            return applicationRepository.findByUser(user, pageable)
                    .map(ApplicationMapper::toResponse);
        }
    }

    // PUT — fetch existing entity, update its fields from request, save, return response DTO
    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        User user = getCurrentUser();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setSalary(request.getSalary());
        application.setStatus(request.getStatus());

        Application updated = applicationRepository.save(application);
        return ApplicationMapper.toResponse(updated);
    }

    public Optional<Application> getById(Long id){
        return applicationRepository.findById(id);
    }

    public boolean existsById(Long id){
        return applicationRepository.existsById(id);
    }

    public void deleteApplicationById(Long id){
        User user = getCurrentUser();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }

        applicationRepository.deleteById(id);
    }

    public Page<Application> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
