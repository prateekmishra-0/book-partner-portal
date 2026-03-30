package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Employee;
import org.springframework.data.rest.core.config.Projection;

/**
 * Shared projection for the 'employee' resource.
 * Dev 5 (YOU) owns this file.
 */
@Projection(name = "employeeSummary", types = {Employee.class})
public interface EmployeeSummaryProjection {

    String getFname();
    String getMinit();
    String getLname();

    // CRITICAL FIX: Restored to satisfy EmployeeApiIntegrationTest.java JSON assertions.
    Integer getJobLvl();
}