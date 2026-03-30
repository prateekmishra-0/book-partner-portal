package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Store;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.StoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Store.class)
public class StoreEventHandler {
    private final StoreRepository storeRepository;
    private final HttpServletRequest request;

    // Constructor Injection
    public StoreEventHandler(StoreRepository storeRepository, HttpServletRequest request) {
        this.storeRepository = storeRepository;
        this.request = request;
    }

    @HandleBeforeCreate
    public void handleStoreBeforeCreate(Store store) {
        String incomingId = store.getStorId();
        String httpMethod = request.getMethod();

        // 1. Defend against POST over-writes : duplicate id
        // post is there id already exsist: 400 Bad Request (ResourceAlreadyExists)
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && storeRepository.existsById(incomingId)) {
                throw new ResourceAlreadyExistsException("Cannot create: A store with ID '" + incomingId + "' already exists.");
            }
        }

        // if put/patch and id is not there-> 404 not found
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (incomingId != null && !storeRepository.existsById(incomingId)) {
                throw new ResourceNotFoundException("Cannot update: Store with ID '" + incomingId + "' does not exist.");
            }
        }
        // The API Safety Bouncer
        store.setIsActive(true);
    }

    /**
     * Fires before UPDATE — triggered by PUT and PATCH to an existing resource.
     */
    @HandleBeforeSave
    public void handleStoreBeforeSave(Store store) {
        // PATCH Null-Safety to protect the soft delete constraint
        if (store.getIsActive() == null) {
            store.setIsActive(true);
        }
    }
}
