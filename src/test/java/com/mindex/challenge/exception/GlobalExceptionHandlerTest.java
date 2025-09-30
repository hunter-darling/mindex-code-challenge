package com.mindex.challenge.exception;

import com.mindex.challenge.data.ErrorResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @Before
  public void setup() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  public void testHandleEmployeeNotFoundException() {
    String employeeId = "test-employee-id";
    EmployeeNotFoundException exception = new EmployeeNotFoundException(employeeId);
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleEmployeeNotFoundException(exception, null);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertEquals("EMPLOYEE_NOT_FOUND", errorResponse.getErrorCode());
    assertEquals("Employee not found with ID: " + employeeId, errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  public void testHandleCompensationNotFoundException() {
    String employeeId = "test-employee-id";
    CompensationNotFoundException exception = new CompensationNotFoundException(employeeId);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleCompensationNotFoundException(exception, null);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertEquals("COMPENSATION_NOT_FOUND", errorResponse.getErrorCode());
    assertEquals("Compensation not found for employee ID: " + employeeId, errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  public void testHandleInvalidRequestException() {
    String errorMessage = "Invalid request data provided";
    InvalidRequestException exception = new InvalidRequestException(errorMessage);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleInvalidRequestException(exception, null);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertEquals("INVALID_REQUEST", errorResponse.getErrorCode());
    assertEquals(errorMessage, errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  public void testHandleGenericException() {
    String errorMessage = "Something went wrong";
    RuntimeException exception = new RuntimeException(errorMessage);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleGenericException(exception, null);

    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getErrorCode());
    assertEquals("An unexpected error occurred. Please try again later.", errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  public void testHandleIllegalArgumentException() {
    String errorMessage = "Invalid argument provided";
    IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleGenericException(exception, null);

    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getErrorCode());
    assertEquals("An unexpected error occurred. Please try again later.", errorResponse.getMessage());
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  public void testErrorResponseTimestamp() {
    EmployeeNotFoundException exception = new EmployeeNotFoundException("test-id");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleEmployeeNotFoundException(exception, null);

    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);
    assertNotNull(errorResponse.getTimestamp());

    // Verify timestamp is within last minute
    long currentTime = System.currentTimeMillis();
    long responseTime = errorResponse.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant()
        .toEpochMilli();
    long timeDifference = Math.abs(currentTime - responseTime);

    assertEquals(true, timeDifference < 60000);
  }
}
