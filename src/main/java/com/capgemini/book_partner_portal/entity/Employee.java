package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @Column(name = "emp_id", length = 9)
    // Enforces format like "PTC11962M" (3 Uppercase, 5 Digits, 1 Uppercase)
    @Pattern(regexp = "^[A-Z]{3}[0-9]{5}[A-Z]$", message = "Invalid Employee ID format")
    private String empId;

    @Column(name = "fname", nullable = false)
    @NotBlank(message = "First name cannot be null or empty")
    private String fname;

    @Column(name = "lname", nullable = false)
    @NotBlank(message = "Last name cannot be null or empty")
    private String lname;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "pub_id")
    private String pubId;

    @Column(name = "job_lvl")
    @NotNull(message = "Job level is required")
    private Integer jobLvl;
}