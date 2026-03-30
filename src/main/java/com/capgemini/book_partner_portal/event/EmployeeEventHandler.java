package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Event handler for the Employee entity.
 * Dev 5 (YOU) owns this file.
 */
@Component
@RepositoryEventHandler(Employee.class)
public class EmployeeEventHandler {

    private final EmployeeRepository employeeRepository;
    private final HttpServletRequest request;

    public EmployeeEventHandler(EmployeeRepository employeeRepository, HttpServletRequest request) {
        this.employeeRepository = employeeRepository;
        this.request = request;
    }

    /**
     * Fires before INSERT — triggered by POST, or by PUT/PATCH to a non-existent resource.
     */
    @HandleBeforeCreate
    public void handleEmployeeBeforeCreate(Employee employee) {
        String incomingId = employee.getEmpId();
        String httpMethod = request.getMethod();

        // Guard 1: The Upsert Hack Defense
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && employeeRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException(
                        "Cannot create: An employee with ID '" + incomingId + "' already exists."
                );
            }
        }

        // Guard 2: Ghost Insert Defense
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !employeeRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException(
                        "Update failed: Employee with ID '" + incomingId + "' was not found."
                );
            }
        }

        // Guard 3: The API Safety Bouncer (Soft Delete Default)
        employee.setIsActive(true);

        // Guard 4: Hire Date Default
        // If the HR client payload omits the hire date, we stamp it with today's date.
        if (employee.getHireDate() == null) {
            employee.setHireDate(LocalDate.now());
        }
    }

    /**
     * Fires before UPDATE — triggered by PUT and PATCH to an existing resource.
     */
    @HandleBeforeSave
    public void handleEmployeeBeforeSave(Employee employee) {
        // Guard 5: PATCH Null-Safety
        // Protects the soft delete constraint during partial updates.
        if (employee.getIsActive() == null) {
            employee.setIsActive(true);
        }
    }
}