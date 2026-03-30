package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for the 'jobs' table. Dev 5 (YOU) owns this file.
 * Endpoint: /api/jobs
 */
@RepositoryRestResource(collectionResourceRel = "jobs", path = "jobs")
public interface JobRepository extends JpaRepository<Job, Short> {
    // The Jobs table is mostly a static lookup table, so we don't need
    // complex search endpoints here unless the UI specifically asks for them.
}