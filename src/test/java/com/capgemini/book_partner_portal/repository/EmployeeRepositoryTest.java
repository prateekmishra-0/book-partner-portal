package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        // Using strict schema data:
        // 1. emp_id matches the CHECK constraint (3 letters, digit 1-9, 4 digits, F/M)
        // 2. pub_id uses actual IDs from the publishers table ("0877" and "1389")
        employeeRepository.save(new Employee("ABC12345M", "Clark", "Kent", LocalDate.now(), "0877", 10));
        employeeRepository.save(new Employee("XYZ98765F", "Bruce", "Wayne", LocalDate.now(), "1389", 10));
    }

    // STRICTLY testing only the findAll() method currently used by the API
    @Test
    public void testFindAll_ShouldReturnSavedEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees).isNotEmpty();

        // Verify our specific test data was successfully mapped and retrieved
        assertThat(employees).anyMatch(e->e.getEmpId().equals("ABC12345M"));
        assertThat(employees).anyMatch(e->e.getEmpId().equals("XYZ98765F"));
    }
}