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

        // If the ID (e.g., 'BU1032') already exists, don't let a POST create it again.
        if (incomingId != null && titleRepository.existsById(incomingId)) {
            throw new ResourceAlreadyExistsException("Cannot create: A book with ID '" + incomingId + "' already exists in the Pubs database.");
        }
       
        // If a user tries to PUT /api/titles/NEW123 but NEW123 doesn't exist, 
        // Spring Data REST normally creates it. This block forces a 404 instead.
        String httpMethod = request.getMethod();
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            throw new ResourceNotFoundException("Update failed: Title ID '" + incomingId + "' not found. You cannot update a non-existent book.");
        }
        
        // Ensure new titles are always created as active
        title.setActive(true);
    }
}