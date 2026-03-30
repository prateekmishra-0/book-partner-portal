package com.capgemini.book_partner_portal.event;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.AuthorRepository;

import jakarta.servlet.http.HttpServletRequest;

@Component
@RepositoryEventHandler(Author.class)
public class AuthorEventHandler {

    private final AuthorRepository authorRepository;
    private final HttpServletRequest request;

    public AuthorEventHandler(AuthorRepository authorRepository, HttpServletRequest request) {
        this.authorRepository = authorRepository;
        this.request = request;
    }

    /**
     * Fires before INSERT — triggered by POST, or by PUT/PATCH to a non-existent resource.
     */
    @HandleBeforeCreate
    public void handleAuthorBeforeCreate(Author author) {
        String incomingId = author.getAuId();
        String httpMethod = request.getMethod();

        // Guard 1: The Upsert Hack Defense
        // Blocks a POST request from overwriting an existing Author.
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && authorRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException(
                    "Cannot create: An author with ID '" + incomingId + "' already exists."
                );
            }
        }

        // Guard 2: Ghost Insert Defense
        // Prevents a PUT/PATCH to a non-existent ID from silently creating a phantom record.
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !authorRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException(
                    "Update failed: Author with ID '" + incomingId + "' was not found."
                );
            }
        }

        // Guard 3: The API Safety Bouncer
        // Ensures all newly created authors are active by default.
        author.setIsActive(true);
    }

    /**
     * Fires before UPDATE — triggered by PUT and PATCH to an existing resource.
     */
    @HandleBeforeSave
    public void handleAuthorBeforeSave(Author author) {
        // Guard 4: PATCH Null-Safety
        // If a client sends a partial PATCH that omits the isActive field, 
        // we must ensure it doesn't default to null and violate the DB constraint.
        if (author.getIsActive() == null) {
            author.setIsActive(true);
        }
    }
}