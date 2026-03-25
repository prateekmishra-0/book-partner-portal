package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String empId;

    @Column(name = "fname", nullable = false)
    private String fname;

    @Column(name = "lname", nullable = false)
    private String lname;

    // Sensitive field we want to hide later
    @Column(name = "hire_date")
    private LocalDate hireDate;

    // Sensitive field we want to hide later
    @Column(name = "pub_id")
    private String pubId;

    @Column(name = "job_lvl")
    private Integer jobLvl;
}