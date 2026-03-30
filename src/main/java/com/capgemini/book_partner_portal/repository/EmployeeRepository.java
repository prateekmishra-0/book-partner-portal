package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.projection.EmployeeSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Repository for the 'employee' table. Dev 5 (YOU) owns this file.
 * Endpoint: /api/employees
 */
@RepositoryRestResource(
        collectionResourceRel = "employees",
        path = "employees",
        excerptProjection = EmployeeSummaryProjection.class
)
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    // --- Search Endpoints mapped to API Tests ---

    // CRITICAL FIX: Restored lname search
    @RestResource(path = "lname", rel = "lname")
    List<Employee> findByLnameContainingIgnoreCase(@Param("lname") String lname);

    // Previously restored fname search
    @RestResource(path = "fname", rel = "by-fname")
    List<Employee> findByFnameContainingIgnoreCase(@Param("fname") String fname);

    // Previously restored joblevel-gt search
    @RestResource(path = "joblevel-gt", rel = "by-joblevel-gt")
    List<Employee> findByJobLvlGreaterThan(@Param("jobLvl") Integer jobLvl);

    // Previously restored joblevel-lt search
    @RestResource(path = "joblevel-lt", rel = "by-joblevel-lt")
    List<Employee> findByJobLvlLessThan(@Param("jobLvl") Integer jobLvl);

    // Cross-Module Support (Verified clean by Claude)
    @RestResource(path = "publisher", rel = "by-publisher")
    List<Employee> findByPubId(@Param("pubId") String pubId);
}