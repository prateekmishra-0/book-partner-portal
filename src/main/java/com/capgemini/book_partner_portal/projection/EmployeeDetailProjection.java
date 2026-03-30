package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Employee;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

/**
 * Detailed projection for the 'employee' resource.
 * Dev 5 (YOU) owns this file.
 *
 * OBJECTIVE: Fulfill the Page 3 requirement (Show publisher name, job post,
 * job level, and hire date) without exposing the entire DB structure.
 */
@Projection(name = "employeeDetail", types = {Employee.class})
public interface EmployeeDetailProjection {

    String getFname();
    String getMinit();
    String getLname();
    Integer getJobLvl();

    // Mapped to LocalDate to match your integration tests!
    LocalDate getHireDate();

    // 1. Fetching data from YOUR Jobs table
    JobView getJob();

    interface JobView {
        String getJobDesc();
        // Omitting min_lvl and max_lvl as the UI doesn't need them
    }

    // 2. Safely fetching data from DEV 4's Publishers table
    PublisherView getPublisher();

    interface PublisherView {
        String getPubName();
    }
}