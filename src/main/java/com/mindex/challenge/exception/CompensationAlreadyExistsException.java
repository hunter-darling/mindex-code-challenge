package com.mindex.challenge.exception;

public class CompensationAlreadyExistsException extends RuntimeException {
  private final String employeeId;

  public CompensationAlreadyExistsException(String employeeId) {
    super("Compensation already exists for employee ID: " + employeeId
        + ". Use the update endpoint to update the compensation.");
    this.employeeId = employeeId;
  }

  public String getEmployeeId() {
    return employeeId;
  }
}
