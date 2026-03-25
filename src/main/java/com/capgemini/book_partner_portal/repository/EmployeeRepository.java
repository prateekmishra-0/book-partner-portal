package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.projection.EmployeeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// Adding excerptProjection forces GET /api/employees to use the mask automatically
@RepositoryRestResource(collectionResourceRel = "employees", path = "employees", excerptProjection = EmployeeSummary.class)
public interface EmployeeRepository extends JpaRepository<Employee, String> {
}