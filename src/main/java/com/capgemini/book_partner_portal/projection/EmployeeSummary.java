package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Employee;
import org.springframework.data.rest.core.config.Projection;

// This is strictly a Spring Data REST Projection for the API layer
@Projection(name = "employeeSummary", types = { Employee.class })
public interface EmployeeSummary {

    String getFname();
    String getLname();
    Integer getJobLvl();

    // By deliberately OMITTING getHireDate() and getPubId() here,
    // Spring Data REST will strip them out of the JSON response.
}