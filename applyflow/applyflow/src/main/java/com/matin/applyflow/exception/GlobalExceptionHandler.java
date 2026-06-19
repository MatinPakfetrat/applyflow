package com.matin.applyflow.exception;

import com.matin.applyflow.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid failures on @RequestBody — collects ALL field violations at once
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors)
        );
    }

    // Invalid enum value in a query param — e.g. ?status=BLAH
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
        String detail = "";
        if(ex.getRequiredType() != null && ex.getRequiredType().isEnum()){
            detail = " Valid values: " + Arrays.toString(ex.getRequiredType().getEnumConstants());
        }

        return ResponseEntity.badRequest().body(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value()
                        , "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'." + detail)
        );
    }

    // Invalid enum value in JSON request body — e.g. { "status": "BLAH" }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex){
        String message = "Malformed request body";

        if(ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()){
            message = "Invalid value '" + ife.getValue() + "'. Valid values: "
                    + Arrays.toString(ife.getTargetType().getEnumConstants());
        }

        return ResponseEntity.badRequest().body(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message)
        );
    }

    // Custom 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage())
        );
    }

    // Catch-all - intentionally vague, never leak internals to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        return ResponseEntity.internalServerError().body(
                new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred")
        );
    }
}
