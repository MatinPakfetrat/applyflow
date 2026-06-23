package com.matin.applyflow.repository;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import com.matin.applyflow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long>{
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    Page<Application> findByStatusAndCompanyNameContainingIgnoreCase(
            ApplicationStatus status,
            String company,
            Pageable pageable
    );

    Page<Application> findByCompanyName(String company, Pageable pageable);

    Page<Application> findAll(Pageable pageable);

    Page<Application> findByUser(User user, Pageable pageable);

    Page<Application> findByUserAndStatus(User user, ApplicationStatus status, Pageable pageable);

    Page<Application> findByUserAndCompanyNameIgnoreCase(User user, String company, Pageable pageable);

    Page<Application> findByUserAndStatusAndCompanyNameIgnoreCase(User user, ApplicationStatus status, String company, Pageable pageable);
}
