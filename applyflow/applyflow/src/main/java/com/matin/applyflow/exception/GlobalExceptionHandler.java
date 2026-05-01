package com.matin.applyflow.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex){
        return ResponseEntity
                .badRequest()
                .body("Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleEnumError(MethodArgumentTypeMismatchException ex) {

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {

            Object[] enumValues = ex.getRequiredType().getEnumConstants();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", "Invalid value for parameter: " + ex.getName(),
                            "invalidValue", ex.getValue(),
                            "validValues", enumValues
                    )
            );
        }

        return ResponseEntity.badRequest().body("Invalid request parameter");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonErrors(HttpMessageNotReadableException ex) {
        Throwable cause = ex;

        while (cause != null) {
            if (cause instanceof InvalidFormatException ife) {
                Class<?> targetType = ife.getTargetType();

                if (targetType != null && targetType.isEnum()) {
                    Object[] enumValues = targetType.getEnumConstants();
                    String field = ife.getPath().stream()
                            .map(ref -> ref.getFieldName())
                            .filter(name -> name != null && !name.isBlank())
                            .reduce((first, second) -> first + "." + second)
                            .orElse("unknown");

                    return ResponseEntity.badRequest().body(
                            Map.of(
                                    "error", "Invalid enum value",
                                    "field", field,
                                    "invalidValue", ife.getValue(),
                                    "validValues", List.of(enumValues)
                            )
                    );
                }
            }

            cause = cause.getCause();
        }

        Throwable rootCause = ex.getMostSpecificCause();

        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", "Malformed JSON request",
                        "details", rootCause.getMessage()
                )
        );
    }
}
