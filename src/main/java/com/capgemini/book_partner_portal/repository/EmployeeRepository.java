package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.projection.EmployeeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

// Adding excerptProjection forces GET /api/employees to use the mask automatically
@RepositoryRestResource(collectionResourceRel = "employees", path = "employees", excerptProjection = EmployeeSummary.class)
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    // 1. Search by First Name (Partial match, case-insensitive)
    @RestResource(path = "fname", rel = "fname")
    List<Employee> findByFnameContainingIgnoreCase(@Param("fname") String fname);

    // 2. Search by Last Name (Partial match, case-insensitive)
    @RestResource(path = "lname", rel = "lname")
    List<Employee> findByLnameContainingIgnoreCase(@Param("lname") String lname);

    // 3. Search for Job Levels GREATER THAN a specific number
    @RestResource(path = "joblevel-gt", rel = "joblevel-gt")
    List<Employee> findByJobLvlGreaterThan(@Param("level") Integer level);

    // 4. Search for Job Levels LESS THAN a specific number
    @RestResource(path = "joblevel-lt", rel = "joblevel-lt")
    List<Employee> findByJobLvlLessThan(@Param("level") Integer level);
}