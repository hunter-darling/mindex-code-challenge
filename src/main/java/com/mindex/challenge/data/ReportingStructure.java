package com.mindex.challenge.data;

public class ReportingStructure {
  private Employee employee;
  private int numberOfReports;

  public ReportingStructure() {
  }

  public ReportingStructure(Employee employee, int numberOfReports) {
    this.employee = employee;
    this.numberOfReports = numberOfReports;
  }

  public Employee getEmployee() {
    return employee;
  }

  public int getNumberOfReports() {
    return numberOfReports;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public void setNumberOfReports(int numberOfReports) {
    this.numberOfReports = numberOfReports;
  }

  @Override
  public String toString() {
    return "ReportingStructure{" +
        "employee=" + employee +
        ", numberOfReports=" + numberOfReports +
        '}';
  }
}
