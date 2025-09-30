package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.CompensationAlreadyExistsException;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.exception.InvalidRequestException;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private CompensationRepository compensationRepository;

  @Override
  public Employee create(Employee employee) {
    LOG.debug("Creating employee [{}]", employee);

    employee.setEmployeeId(UUID.randomUUID().toString());
    employeeRepository.insert(employee);

    return employee;
  }

  @Override
  public Employee read(String id) {
    LOG.debug("Creating employee with id [{}]", id);

    Employee employee = employeeRepository.findByEmployeeId(id);

    if (employee == null) {
      throw new EmployeeNotFoundException(id);
    }

    return employee;
  }

  @Override
  public Employee update(Employee employee) {
    LOG.debug("Updating employee [{}]", employee);

    return employeeRepository.save(employee);
  }

  @Override
  public ReportingStructure getReportingStructure(String id) {
    LOG.debug("Getting reporting structure for employee [{}]", id);

    Employee employee = employeeRepository.findByEmployeeId(id);
    if (employee == null) {
      throw new EmployeeNotFoundException(id);
    }

    int numberOfReports = calculateNumberOfReports(employee);

    return new ReportingStructure(employee, numberOfReports);
  }

  @Override
  public Compensation getCompensation(String id) {
    LOG.debug("Getting compensation for employee [{}]", id);

    Employee employee = employeeRepository.findByEmployeeId(id);
    if (employee == null) {
      throw new EmployeeNotFoundException(id);
    }

    Compensation compensation = compensationRepository.findCompensationByEmployeeId(id);
    if (compensation == null) {
      throw new CompensationNotFoundException(id);
    }

    return compensation;
  }

  @Override
  public Compensation createCompensation(String id, Compensation compensation) {
    LOG.debug("Creating compensation for employee [{}]", id);

    Employee employee = employeeRepository.findByEmployeeId(id);
    if (employee == null) {
      throw new EmployeeNotFoundException(id);
    }

    Compensation existingCompensation = compensationRepository.findCompensationByEmployeeId(id);
    if (!compensation.getEmployeeId().equals(id)) {
      throw new InvalidRequestException("EmployeeId in request body does not match employeeId in path");
    }
    if (existingCompensation != null) {
      throw new CompensationAlreadyExistsException(id);
    }

    if (compensation.getEffectiveDate() == null) {
      compensation.setEffectiveDate(LocalDate.now().toString());
    }

    return compensationRepository.insert(compensation);
  }

  @Override
  public Compensation updateCompensation(String id, Compensation compensation) {
    LOG.debug("Updating compensation for employee [{}]", id);

    Compensation existingCompensation = compensationRepository.findCompensationByEmployeeId(id);
    if (existingCompensation == null) {
      throw new CompensationNotFoundException(id);
    }
    if (!compensation.getEmployeeId().equals(id)) {
      throw new InvalidRequestException("EmployeeId in request body does not match employeeId in path");
    }

    compensation.setEffectiveDate(LocalDate.now().toString());

    return compensationRepository.save(compensation);
  }

  private int calculateNumberOfReports(Employee employee) {
    // If the employee has no direct reports, return 0
    if (employee.getDirectReports() == null || employee.getDirectReports().isEmpty()) {
      return 0;
    }

    // Initialize the number of total reports to the number of direct reports for
    // employee in question
    int numberOfReports = employee.getDirectReports().size();

    // Recursively calculate the number of reports for each direct report
    for (Employee directReport : employee.getDirectReports()) {
      Employee fullDirectReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
      if (fullDirectReport != null) {
        numberOfReports += calculateNumberOfReports(fullDirectReport);
      }
    }

    return numberOfReports;
  }
}
