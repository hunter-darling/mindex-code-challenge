package com.mindex.challenge.data;

public class Compensation {
  private double salary;
  private String effectiveDate;
  private String employeeId;

  public Compensation(double salary, String effectiveDate, String employeeId) {
    this.salary = salary;
    this.effectiveDate = effectiveDate;
    this.employeeId = employeeId;
  }

  public double getSalary() {
    return salary;
  }

  public String getEffectiveDate() {
    return effectiveDate;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setSalary(double salary) {
    this.salary = salary;
  }

  public void setEffectiveDate(String effectiveDate) {
    this.effectiveDate = effectiveDate;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }
}
