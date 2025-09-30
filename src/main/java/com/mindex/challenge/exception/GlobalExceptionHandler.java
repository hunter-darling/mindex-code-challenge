package com.mindex.challenge.exception;

import com.mindex.challenge.data.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EmployeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(
      EmployeeNotFoundException ex, WebRequest request) {
    LOG.warn("Employee not found: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "EMPLOYEE_NOT_FOUND",
        ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CompensationNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCompensationNotFoundException(
      CompensationNotFoundException ex, WebRequest request) {
    LOG.warn("Compensation not found: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "COMPENSATION_NOT_FOUND",
        ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CompensationAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleCompensationAlreadyExistsException(
      CompensationAlreadyExistsException ex, WebRequest request) {
    LOG.warn("Compensation already exists: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "COMPENSATION_ALREADY_EXISTS",
        ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequestException(
      InvalidRequestException ex, WebRequest request) {
    LOG.warn("Invalid request: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "INVALID_REQUEST",
        ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {
    LOG.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse = new ErrorResponse(
        "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred. Please try again later.");

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
