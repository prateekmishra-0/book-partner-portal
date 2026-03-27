package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
// 1. Hijack the DELETE command and turn it into an UPDATE
@SQLDelete(sql = "UPDATE employee SET is_active = false WHERE emp_id = ?")
// 2. Secretly append "WHERE is_active = true" to every single SELECT query
@SQLRestriction("is_active = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @Column(name = "emp_id", length = 9, updatable = false)
    @Pattern(regexp = "^[A-Z]{3}[0-9]{5}[A-Z]$", message = "Invalid Employee ID format")
    private String empId;

    @Column(name = "fname", nullable = false)
    @NotBlank(message = "First name cannot be null or empty")
    private String fname;

    @Column(name = "lname", nullable = false)
    @NotBlank(message = "Last name cannot be null or empty")
    private String lname;

    @Column(name = "hire_date", updatable = false)
    private LocalDate hireDate = LocalDate.now();

    @Column(name = "pub_id")
    private String pubId = "9952";

    @Column(name = "job_lvl")
    @NotNull(message = "Job level is required")
    private Integer jobLvl;

    // 3. Map the new database column so Hibernate knows it exists
    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}