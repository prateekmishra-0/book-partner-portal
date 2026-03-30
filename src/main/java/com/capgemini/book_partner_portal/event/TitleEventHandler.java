package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.TitleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Event handler for the Title entity.
 * Dev 1 owns this file.
 *
 * WHY HttpServletRequest IS REQUIRED (not an anti-pattern):
 * Spring Data REST event routing is based on JPA entity state, NOT HTTP verbs:
 *   @HandleBeforeCreate → fires when the entity is NEW (no existing record in DB)
 *   @HandleBeforeSave   → fires when the entity EXISTS (found in DB)
 *
 * The problem: PUT /api/titles/GHOST999 (non-existent ID) fires @HandleBeforeCreate
 * because the entity doesn't exist yet — Spring Data REST cannot tell the difference
 * between a legitimate POST and a misdirected PUT at the JPA state level alone.
 *
 * HttpServletRequest.getMethod() is the ONLY reliable way to detect this scenario
 * inside an event handler. We use it for the ghost insert guard only — all other
 * logic is standard event handler code.
 *
 * LIFECYCLE:
 *   POST  /api/titles        → @HandleBeforeCreate → guards 1, 2, 3 below
 *   PUT   /api/titles/{id}   → @HandleBeforeSave   → guard 4 below
 *   PATCH /api/titles/{id}   → @HandleBeforeSave   → guard 4 below
 *   DELETE /api/titles/{id}  → @SQLDelete rewrites to UPDATE is_active=false
 *                               (no event handler needed — DB handles it)
 */
@Component
@RepositoryEventHandler(Title.class)
public class TitleEventHandler {

    private final TitleRepository titleRepository;
    private final HttpServletRequest request;

    public TitleEventHandler(TitleRepository titleRepository, HttpServletRequest request) {
        this.titleRepository = titleRepository;
        this.request = request;
    }

    /**
     * Fires before INSERT — triggered by POST, or by PUT/PATCH to a non-existent resource.
     *
     * Guard 1 — Upsert Hack Defense:
     *   Blocks a POST that provides a titleId already in the database.
     *   Without this, Spring Data REST silently executes an UPDATE, overwriting
     *   the existing book. This is a data corruption scenario.
     *
     * Guard 2 — Ghost Insert Defense:
     *   When a client sends PUT /api/titles/NONEXISTENT, Spring Data REST fires
     *   @HandleBeforeCreate (because the entity is new from JPA's perspective).
     *   Without HttpServletRequest, we cannot distinguish this from a real POST.
     *   We detect the method and throw 404 to prevent phantom record creation.
     *
     * Guard 3 — Safety Bouncer:
     *   Spec requirement: "API ignores isActive: false in POST requests to prevent
     *   malicious pre-deletion of records."
     *   isActive is @JsonIgnore so the client cannot send it, but we enforce
     *   it defensively here regardless.
     */
    @HandleBeforeCreate
    public void handleTitleBeforeCreate(Title title) {
        String incomingId = title.getTitleId();
        String httpMethod = request.getMethod();

        // Guard 1: Block POST that tries to overwrite an existing active title
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && titleRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException(
                    "Cannot create: A book with ID '" + incomingId + "' already exists."
                );
            }
        }

        // Guard 2: Ghost insert — PUT/PATCH fired BeforeCreate (target does not exist)
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId == null || !titleRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException(
                    "Update failed: Title with ID '" + incomingId + "' was not found."
                );
            }
        }

        // Guard 3: Safety Bouncer — new titles are always active
        title.setIsActive(true);
    }

    /**
     * Fires before UPDATE — triggered by PUT and PATCH to an existing resource.
     *
     * Guard 4 — isActive Null Safety:
     *   A PATCH request sends only changed fields. If isActive is not in the PATCH
     *   body, Hibernate may merge a null into the entity. Since isActive is NOT NULL
     *   in the DB, this would cause a ConstraintViolationException.
     *   We defensively restore it to true.
     *
     * NOTE on pubdate immutability:
     *   updatable=false on @Column(name="pubdate") means Hibernate silently ignores
     *   any pubdate in the request body. No code needed here for that constraint.
     */
    @HandleBeforeSave
    public void handleTitleBeforeSave(Title title) {
        if (title.getIsActive() == null) {
            title.setIsActive(true);
        }
    }
}
