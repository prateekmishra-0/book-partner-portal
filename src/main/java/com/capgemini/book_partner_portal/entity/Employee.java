package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entity for the 'employee' table. Dev 5 (YOU) owns this.
 */
@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE employee SET is_active = false WHERE emp_id = ?")
@SQLRestriction("is_active = true")
public class Employee {

    @Id
    @Column(name = "emp_id", nullable = false, length = 10, updatable = false)
    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^([A-Z]{3}[1-9][0-9]{4}[FM]|[A-Z]-[A-Z][1-9][0-9]{4}[FM])$",
            message = "Employee ID must match the standard corporate format")
    private String empId;

    @Column(name = "fname", length = 20, nullable = false)
    @NotBlank(message = "First name is required")
    @Size(max = 20)
    private String fname;

    @Column(name = "minit", columnDefinition = "CHAR(1)")
    private String minit;

    @Column(name = "lname", length = 30, nullable = false)
    @NotBlank(message = "Last name is required")
    @Size(max = 30)
    private String lname;

    @Column(name = "job_id", nullable = false)
    private Short jobId = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private Job job;

    @Column(name = "job_lvl")
    private Integer jobLvl = 10;

    @Column(name = "pub_id", nullable = false, length = 4)
    private String pubId = "9952";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pub_id", insertable = false, updatable = false)
    private Publisher publisher;

    // CRITICAL FIX: Added '= LocalDate.now()' to satisfy programmatic test saves.
    @Column(name = "hire_date", nullable = false, updatable = false)
    private LocalDate hireDate = LocalDate.now();

    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;



    // Custom constructor for testing to bypass the @ManyToOne relationship fields
    public Employee(String empId, String fname, String lname, LocalDate hireDate, String pubId, Integer jobLvl, Boolean isActive) {
        this.empId = empId;
        this.fname = fname;
        this.lname = lname;
        this.hireDate = hireDate;
        this.pubId = pubId;
        this.jobLvl = jobLvl;
        this.isActive = isActive;
    }
}