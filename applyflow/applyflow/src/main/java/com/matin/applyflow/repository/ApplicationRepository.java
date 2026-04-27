package com.matin.applyflow.repository;

import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long>{
    List<Application> findByStatus(ApplicationStatus status);
}
