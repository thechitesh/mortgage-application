package com.myorg.mortgage.exception;

import com.myorg.mortgage.app.model.ErrorDto;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    public void testHandleAuthExceptions() {
        Exception exception = new JwtException("JWT error");
        ResponseEntity<Object> response = globalExceptionHandler.handleAuthExceptions(exception, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorDto error = (ErrorDto) response.getBody();
        assertNotNull(error);
        assertEquals("JWT error", error.getMessage());
        assertEquals("ERR-001", error.getCode());
    }

    @Test
    public void testHandleSQLExceptions() {
        Exception exception = new SQLException("SQL error");
        ResponseEntity<Object> response = globalExceptionHandler.handleSQLExceptions(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDto error = (ErrorDto) response.getBody();
        assertNotNull(error);
        assertEquals("Bad Request", error.getMessage());
        assertEquals("ERR-002", error.getCode());
    }

    @Test
    public void testHandleRuntimeExceptions() {
        RuntimeException exception = new RuntimeException("Runtime error");
        ResponseEntity<Object> response = globalExceptionHandler.handleRuntimeExceptions(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorDto error = (ErrorDto) response.getBody();
        assertNotNull(error);
        assertEquals("Something went wrong", error.getMessage());
        assertEquals("ERR-003", error.getCode());
    }

    @Test
    public void testHandleAllExceptions() {
        Exception exception = new Exception("General error");
        when(webRequest.getAttribute("filter.exception", WebRequest.SCOPE_REQUEST)).thenReturn(null);
        ResponseEntity<String> response = globalExceptionHandler.handleAllExceptions(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: General error", response.getBody());
    }

    @Test
    public void testHandleAllExceptionsWithFilterException() {
        Exception exception = new Exception("General error");
        Exception filterException = new Exception("Filter error");
        when(webRequest.getAttribute("filter.exception", WebRequest.SCOPE_REQUEST)).thenReturn(filterException);
        ResponseEntity<String> response = globalExceptionHandler.handleAllExceptions(exception, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Filter exception: Filter error", response.getBody());
    }

}
