package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
      throw new RuntimeException("Invalid employeeId: " + id);
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
      throw new RuntimeException("Invalid employeeId: " + id);
    }
    int numberOfReports = calculateNumberOfReports(employee);
    return new ReportingStructure(employee, numberOfReports);
  }

  @Override
  public Compensation getCompensation(String id) {
    LOG.debug("Getting compensation for employee [{}]", id);
    Employee employee = employeeRepository.findByEmployeeId(id);
    if (employee == null) {
      throw new RuntimeException("Invalid employeeId: " + id);
    }
    return compensationRepository.findCompensationByEmployeeId(id);
  }

  @Override
  public Compensation createCompensation(String id, Compensation compensation) {
    LOG.debug("Creating compensation for employee [{}]", id);
    Compensation existingCompensation = compensationRepository.findCompensationByEmployeeId(id);
    if (!compensation.getEmployeeId().equals(id)) {
      throw new RuntimeException("EmployeeId in compensation does not match employeeId in request");
    }
    if (existingCompensation != null) {
      throw new RuntimeException("Compensation already exists for employeeId: " + id);
    }
    return compensationRepository.insert(compensation);
  }

  @Override
  public Compensation updateCompensation(String id, Compensation compensation) {
    LOG.debug("Updating compensation for employee [{}]", id);
    Compensation existingCompensation = compensationRepository.findCompensationByEmployeeId(id);
    if (existingCompensation == null) {
      throw new RuntimeException("Compensation not found for employeeId: " + id);
    }
    if (!compensation.getEmployeeId().equals(id)) {
      throw new RuntimeException("EmployeeId in compensation does not match employeeId in request");
    }
    return compensationRepository.save(compensation);
  }

  private int calculateNumberOfReports(Employee employee) {
    if (employee.getDirectReports() == null || employee.getDirectReports().isEmpty()) {
      return 0;
    }
    int numberOfReports = employee.getDirectReports().size();
    for (Employee directReport : employee.getDirectReports()) {
      Employee fullDirectReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
      if (fullDirectReport != null) {
        numberOfReports += calculateNumberOfReports(fullDirectReport);
      }
    }
    return numberOfReports;
  }
}
