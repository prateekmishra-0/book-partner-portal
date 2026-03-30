package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Job;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.JobRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Event handler for the Job entity.
 * Dev 5 (YOU) owns this file.
 *
 * NOTE: The jobs table has NO is_active column, so there is no soft-delete
 * flag to protect via @HandleBeforeSave null-checks.
 */
@Component
@RepositoryEventHandler(Job.class)
public class JobEventHandler {

    private final JobRepository jobRepository;
    private final HttpServletRequest request;

    public JobEventHandler(JobRepository jobRepository, HttpServletRequest request) {
        this.jobRepository = jobRepository;
        this.request = request;
    }

    /**
     * Fires before a new Job is inserted into the database.
     */
    @HandleBeforeCreate
    public void handleJobBeforeCreate(Job job) {
        Short incomingId = job.getJobId();
        String httpMethod = request.getMethod();

        // Guard 1: Defensive Payload Protection
        // Even though job_id is auto-incremented, a rogue API client might try to pass
        // an existing ID in the POST body. We reject it here.
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && jobRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException(
                        "Cannot create: A job with ID '" + incomingId + "' already exists. " +
                                "Do not provide an ID when creating a new job role."
                );
            }
        }

        // Guard 2: Ghost Insert Defense
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !jobRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException(
                        "Update failed: Job with ID '" + incomingId + "' was not found."
                );
            }
        }
    }
}