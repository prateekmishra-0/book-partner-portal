package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.PublisherRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Event handler for the Publisher entity.
 * Dev 4 is the SOLE OWNER of this file.
 */
@Component
@RepositoryEventHandler(Publisher.class)
public class PublisherEventHandler {

    private final PublisherRepository publisherRepository;
    private final HttpServletRequest request;

    public PublisherEventHandler(PublisherRepository publisherRepository, HttpServletRequest request) {
        this.publisherRepository = publisherRepository;
        this.request = request;
    }

    /**
     * Fires before INSERT — triggered by POST, or by PUT/PATCH to a non-existent resource.
     */
    @HandleBeforeCreate
    public void handlePublisherBeforeCreate(Publisher publisher) {
        String incomingId = publisher.getPubId();
        String httpMethod = request.getMethod();

        // Guard 1: The Upsert Hack Defense
        // If the client provides a pub_id that already exists, reject the POST.
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && publisherRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException(
                        "Cannot create: A publisher with ID '" + incomingId + "' already exists."
                );
            }
        }

        // Guard 2: Ghost Insert Defense
        // If the client sends a PUT/PATCH to a non-existent publisher, block the phantom creation.
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !publisherRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException(
                        "Update failed: Publisher with ID '" + incomingId + "' was not found."
                );
            }
        }

        // Guard 3: The API Safety Bouncer
        publisher.setIsActive(true);
    }

    /**
     * Fires before UPDATE — triggered by PUT and PATCH to an existing resource.
     */
    @HandleBeforeSave
    public void handlePublisherBeforeSave(Publisher publisher) {
        // Guard 4: PATCH Null-Safety
        if (publisher.getIsActive() == null) {
            publisher.setIsActive(true);
        }
    }
}