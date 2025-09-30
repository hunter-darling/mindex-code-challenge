package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.exception.InvalidRequestException;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.dao.CompensationRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

  private String employeeUrl;
  private String employeeIdUrl;
  private String employeeIdReportingStructureUrl;
  private String employeeIdCompensationUrl;

  @Autowired
  private EmployeeService employeeService;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private CompensationRepository compensationRepository;

  @Before
  public void setup() {
    employeeUrl = "http://localhost:" + port + "/employee";
    employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    employeeIdReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
    employeeIdCompensationUrl = "http://localhost:" + port + "/employee/{id}/compensation";
    // Clean up compensation repository before each test
    compensationRepository.deleteAll();
  }

  @Test
  public void testCreateReadUpdate() {
    Employee testEmployee = new Employee();
    testEmployee.setFirstName("John");
    testEmployee.setLastName("Doe");
    testEmployee.setDepartment("Engineering");
    testEmployee.setPosition("Developer");

    // Create checks
    Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

    assertNotNull(createdEmployee.getEmployeeId());
    assertEmployeeEquivalence(testEmployee, createdEmployee);

    // Read checks
    Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId())
        .getBody();
    assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
    assertEmployeeEquivalence(createdEmployee, readEmployee);

    // Update checks
    readEmployee.setPosition("Development Manager");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Employee updatedEmployee = restTemplate.exchange(employeeIdUrl,
        HttpMethod.PUT,
        new HttpEntity<Employee>(readEmployee, headers),
        Employee.class,
        readEmployee.getEmployeeId()).getBody();

    assertEmployeeEquivalence(readEmployee, updatedEmployee);
  }

  @Test
  public void testReportingStructure() {
    // John Lennon has 4 reports
    ReportingStructure reportingStructure = restTemplate
        .getForEntity(employeeIdReportingStructureUrl, ReportingStructure.class, "16a596ae-edd3-4847-99fe-c4518e82c86f")
        .getBody();
    assertNotNull(reportingStructure);
    assertEquals(4, reportingStructure.getNumberOfReports());
  }

  @Test
  public void testCreateReadCompensation() {
    // Create a new employee
    Employee uniqueEmployee = createUniqueEmployee("CompTest");
    Employee createdEmployee = restTemplate.postForEntity(employeeUrl, uniqueEmployee, Employee.class).getBody();
    assertNotNull(createdEmployee);
    String createdEmployeeId = createdEmployee.getEmployeeId();

    // Create a new compensation
    Compensation employeeCompensation = new Compensation();
    employeeCompensation.setSalary(100000.0);
    employeeCompensation.setEmployeeId(createdEmployeeId);
    restTemplate.postForEntity(employeeIdCompensationUrl, employeeCompensation, Compensation.class, createdEmployeeId);

    // Verify the created compensation
    Compensation createdCompensation = restTemplate
        .getForEntity(employeeIdCompensationUrl, Compensation.class, createdEmployeeId)
        .getBody();
    assertNotNull(createdCompensation);
    assertEquals(100000.0, createdCompensation.getSalary(), 0.01);
    assertEquals(LocalDate.now().toString(), createdCompensation.getEffectiveDate());
    assertEquals(createdEmployeeId, createdCompensation.getEmployeeId());
  }

  @Test
  public void testEmployeeNotFoundException() {
    // Test that an exception is thrown if the employee ID does not exist
    assertThrows(EmployeeNotFoundException.class, () -> employeeService.read("nonexistent-id"));
    assertThrows(EmployeeNotFoundException.class, () -> employeeService.getReportingStructure("nonexistent-id"));
    assertThrows(EmployeeNotFoundException.class, () -> employeeService.getCompensation("nonexistent-id"));
    Compensation compensation = new Compensation();
    compensation.setEmployeeId("nonexistent-id");
    assertThrows(EmployeeNotFoundException.class,
        () -> employeeService.createCompensation("nonexistent-id", compensation));
  }

  @Test
  public void testInvalidRequestException() {
    // Test that an exception is thrown if the employee ID does not match the
    // compensation ID
    Compensation compensation = new Compensation();
    compensation.setEmployeeId("nonexistent-eye-dee");
    assertThrows(InvalidRequestException.class,
        () -> employeeService.createCompensation("nonexistent-id", compensation));
  }

  @Test
  public void testCompensationNotFoundException() {
    // Create an employee first to avoid the EmployeeNotFoundException
    Employee uniqueEmployee = createUniqueEmployee("NoComp");
    Employee createdEmployee = restTemplate.postForEntity(employeeUrl, uniqueEmployee, Employee.class).getBody();
    assertNotNull(createdEmployee);
    String createdEmployeeId = createdEmployee.getEmployeeId();

    // Test that an exception is thrown if the compensation does not exist for an
    // existing employee
    assertThrows(CompensationNotFoundException.class, () -> employeeService.getCompensation(createdEmployeeId));
  }

  private Employee createUniqueEmployee(String testName) {
    // Create a new employee on a test-by-test basis
    Employee employee = new Employee();
    employee.setFirstName("John");
    employee.setLastName(testName);
    employee.setDepartment("Engineering");
    employee.setPosition("Developer");
    return employee;
  }

  private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
    assertEquals(expected.getFirstName(), actual.getFirstName());
    assertEquals(expected.getLastName(), actual.getLastName());
    assertEquals(expected.getDepartment(), actual.getDepartment());
    assertEquals(expected.getPosition(), actual.getPosition());
  }
}
