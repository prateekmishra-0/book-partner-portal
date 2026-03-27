package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.EmployeeRepository;
import com.capgemini.book_partner_portal.repository.PublisherRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Publisher.class)
public class PublisherEventHandler {

    private final PublisherRepository publisherRepository;
    private final HttpServletRequest request; // Gives us access to the HTTP network traffic

    public PublisherEventHandler(PublisherRepository publisherRepository, HttpServletRequest request) {
        this.publisherRepository = publisherRepository;
        this.request = request;
    }

    @HandleBeforeCreate
    public void handlePublisherBeforeCreate(Publisher publisher) {
        String incomingId = publisher.getPubId();

        // 1. Defend against POST over-writes (The Upsert Hack)
        if (incomingId != null && publisherRepository.existsById(incomingId)) {
            throw new ResourceAlreadyExistsException("Cannot create: An publisher with ID '" + incomingId + "' already exists.");
        }

        // 2. Defend against PUT/PATCH phantom inserts
        // If we are in "BeforeCreate" but the user used PUT or PATCH, they are trying to update a ghost!
        String httpMethod = request.getMethod();
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {

            // We throw Spring Data REST's built-in ResourceNotFoundException.
            // Your GlobalExceptionHandler is already wired up to catch this and return a 404!
            throw new ResourceNotFoundException("Cannot update: Publisher with ID '" + incomingId + "' does not exist.");
        }
    }
}