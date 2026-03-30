package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.TitleAuthor;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.TitleAuthorRepository;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Event handler for the TitleAuthor join entity.
 * Dev 1 owns this file.
 */
@Component
@RepositoryEventHandler(TitleAuthor.class)
public class TitleAuthorEventHandler {

    private final TitleAuthorRepository titleAuthorRepository;

    public TitleAuthorEventHandler(TitleAuthorRepository titleAuthorRepository) {
        this.titleAuthorRepository = titleAuthorRepository;
    }

    /**
     * Fires automatically before a new Author-Title link is created (POST request).
     */
    @HandleBeforeCreate
    public void handleTitleAuthorBeforeCreate(TitleAuthor titleAuthor) {
        // Guard 1: Prevent Duplicate Links
        // Blocks the client from linking the exact same author to the exact same book twice.
        if (titleAuthor.getId() != null && titleAuthorRepository.existsById(titleAuthor.getId())) {
            throw new ResourceAlreadyExistsException(
                "Cannot create: Author '" + titleAuthor.getId().getAuId() + 
                "' is already linked to Title '" + titleAuthor.getId().getTitleId() + "'."
            );
        }
    }
}