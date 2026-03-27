package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.TitleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Title.class)
public class TitleEventHandler {

    private final TitleRepository titleRepository;
    private final HttpServletRequest request;

    public TitleEventHandler(TitleRepository titleRepository, HttpServletRequest request) {
        this.titleRepository = titleRepository;
        this.request = request;
    }

    @HandleBeforeCreate
    public void handleTitleBeforeCreate(Title title) {
        String incomingId = title.getTitleId();

        // 1. POST Check: Prevent Duplicate ID (Upsert Hack)
        String httpMethod = request.getMethod();
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && titleRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException("Cannot create: A book with ID '" + incomingId + "' already exists.");
            }
        }

        // Logic: If I am updating but the ID doesn't exist in DB, throw 404.
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !titleRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException("Update failed: Title ID '" + incomingId + "' not found.");
            }
        }

        // 3. Force Active State
        title.setIsActive(true); // Fixed: Use the setter name from your entity
    }
}