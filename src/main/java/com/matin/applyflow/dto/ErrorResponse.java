package com.matin.applyflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timeStamp;

    // For simple errors: 404s, enum mismatches, generic failures
    public ErrorResponse(int status, String message){
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    // For validation failures: carries a list of field-level messages
    public ErrorResponse(int status, String message, List<String> errors){
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
