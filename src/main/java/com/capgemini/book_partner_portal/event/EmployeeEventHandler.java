package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Employee.class)
public class EmployeeEventHandler {

    private final EmployeeRepository employeeRepository;
    private final HttpServletRequest request; // Gives us access to the HTTP network traffic

    public EmployeeEventHandler(EmployeeRepository employeeRepository, HttpServletRequest request) {
        this.employeeRepository = employeeRepository;
        this.request = request;
    }

    @HandleBeforeCreate
    public void handleEmployeeBeforeCreate(Employee employee) {
        String incomingId = employee.getEmpId();

        // 1. Defend against POST over-writes (The Upsert Hack)
        if (incomingId != null && employeeRepository.existsById(incomingId)) {
            throw new ResourceAlreadyExistsException("Cannot create: An employee with ID '" + incomingId + "' already exists.");
        }

        // 2. Defend against PUT/PATCH phantom inserts
        // If we are in "BeforeCreate" but the user used PUT or PATCH, they are trying to update a ghost!
        String httpMethod = request.getMethod();
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {

            // We throw Spring Data REST's built-in ResourceNotFoundException.
            // Your GlobalExceptionHandler is already wired up to catch this and return a 404!
            throw new ResourceNotFoundException("Cannot update: Employee with ID '" + incomingId + "' does not exist.");
        }
    }
}