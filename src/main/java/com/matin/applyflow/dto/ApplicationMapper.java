package com.matin.applyflow.dto;

import com.matin.applyflow.model.Application;

public class ApplicationMapper {

    // Converts a request DTO into a new entity (used on POST)
    public static Application toEntity(ApplicationRequest request){
        Application application = new Application();
        application.setStatus(request.getStatus());
        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setSalary(request.getSalary());
        return application;
    }

    // Converts a saved entity into a response DTO (used on GET/POST/PUT responses)
    public static ApplicationResponse toResponse(Application application){
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        response.setCompanyName(application.getCompanyName());
        response.setSalary(application.getSalary());
        response.setJobTitle(application.getJobTitle());
        response.setStatus(application.getStatus());
        return response;
    }

    private ApplicationMapper(){}
}
