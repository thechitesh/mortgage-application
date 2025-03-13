package com.myorg.mortgage.exception;

import com.myorg.mortgage.app.model.ErrorDto;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {JwtException.class, AccessDeniedException.class, UsernameNotFoundException.class, MortgageAuthenticationException.class})
    protected ResponseEntity<Object> handleAuthExceptions(Exception exception, WebRequest request) {
        log.error("Auth Exception :" , exception);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorDto error = new ErrorDto(exception.getMessage(), "ERR-001");
        return handleExceptionInternal(exception, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class, SQLException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleSQLExceptions(Exception exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("Exception :" , exception);
        ErrorDto error = new ErrorDto("Bad Request", "ERR-002");
        return handleExceptionInternal(exception, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeExceptions(RuntimeException exception, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("Exception :" , exception);
        ErrorDto error = new ErrorDto("Something went wrong", "ERR-003");
        return handleExceptionInternal(exception, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        Exception filterException = (Exception) request.getAttribute("filter.exception", WebRequest.SCOPE_REQUEST);
        if (filterException != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Filter exception: " + filterException.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

}
