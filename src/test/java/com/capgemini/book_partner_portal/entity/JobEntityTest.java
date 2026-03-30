package com.capgemini.book_partner_portal.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JobEntityTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidJob_ShouldPassValidation() {
        // ID is auto-generated, so we pass null for it
        Job job = new Job(null, "Senior Developer", 100, 200);
        Set<ConstraintViolation<Job>> violations = validator.validate(job);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testInvalidMinLvl_ShouldFailValidation() {
        Job job = new Job(null, "Intern", 5, 50); // Min level must be >= 10
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("minLvl"));
    }

    @Test
    public void testInvalidMaxLvl_ShouldFailValidation() {
        Job job = new Job(null, "CEO", 200, 300); // Max level must be <= 250
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maxLvl"));
    }

    @Test
    public void testBlankJobDesc_ShouldFailValidation() {
        Job job = new Job(null, "", 50, 100);
        Set<ConstraintViolation<Job>> violations = validator.validate(job);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("jobDesc"));
    }
}