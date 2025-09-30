package com.mindex.challenge.exception;

public class CompensationNotFoundException extends RuntimeException {
  private final String employeeId;

  public CompensationNotFoundException(String employeeId) {
    super("Compensation not found for employee ID: " + employeeId);
    this.employeeId = employeeId;
  }

  public String getEmployeeId() {
    return employeeId;
  }
}
