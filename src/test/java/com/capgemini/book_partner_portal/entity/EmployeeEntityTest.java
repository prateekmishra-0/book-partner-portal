package com.capgemini.book_partner_portal.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeEntityTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Test 1
    @Test
    public void testValidEmployeeId_ShouldPassValidation() {
        Employee emp = new Employee("PTC11962M", "Prateek", "Mishra", LocalDate.now(), "PUB-1", 3,true);
        Set<ConstraintViolation<Employee>> violations = validator.validate(emp);
        assertThat(violations).isEmpty(); // No errors expected
    }

    // Test 2
    @Test
    public void testInvalidEmployeeId_ShouldFailRegexValidation() {
        Employee emp = new Employee("12345", "Prateek", "Mishra", LocalDate.now(), "PUB-1", 3, true);
        Set<ConstraintViolation<Employee>> violations = validator.validate(emp);

        assertThat(violations).isNotEmpty();
        // We expect the violation to be specifically on the empId field
        boolean hasEmpIdError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("empId"));
        assertThat(hasEmpIdError).isTrue();
    }

    // Test 3
    @Test
    public void testMissingMandatoryFields_ShouldFailValidation() {
        Employee emp = new Employee("PTC11962M", null, "", LocalDate.now(), "PUB-1", null, true);
        Set<ConstraintViolation<Employee>> violations = validator.validate(emp);

        assertThat(violations).isNotEmpty();
        // Expecting errors on fname and lname because they shouldn't be null/empty
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("fname"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lname"));
    }
}