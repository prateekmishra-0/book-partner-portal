package com.capgemini.book_partner_portal.event;

import com.capgemini.book_partner_portal.entity.Sale;
import com.capgemini.book_partner_portal.exception.ResourceAlreadyExistsException;
import com.capgemini.book_partner_portal.repository.SaleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Event handler for the Sale transaction entity.

 * WHY NO @HandleBeforeSave HERE?
 * The 'sales' table models financial transactions and does NOT have an is_active
 * soft-delete column. Furthermore, financial line items are generally immutable.
 * If an update does occur, there is no soft-delete flag to protect via null-checks,
 * so @HandleBeforeSave is unnecessary for this specific entity.
 */
@Component
@RepositoryEventHandler(Sale.class)
public class SaleEventHandler {

    private final SaleRepository saleRepository;
    private final HttpServletRequest request;

    public SaleEventHandler(SaleRepository saleRepository, HttpServletRequest request) {
        this.saleRepository = saleRepository;
        this.request = request;
    }

    /**
     * Fires before a new financial transaction is recorded.
     */
    @HandleBeforeCreate
    public void handleSaleBeforeCreate(Sale sale) {
        String httpMethod = request.getMethod();

        // Guard 1: Composite Key Upsert Defense
        if ("POST".equalsIgnoreCase(httpMethod)) {
            if (sale.getId() != null && saleRepository.existsById(sale.getId())) {
                throw new ResourceAlreadyExistsException(
                        "Cannot create: A sale record for Order '" + sale.getId().getOrdNum() +
                                "' at Store '" + sale.getId().getStorId() +
                                "' for Book '" + sale.getId().getTitleId() + "' already exists."
                );
            }
        }

        // Guard 2: Ghost Insert Defense on the Composite Key
        if ("PUT".equalsIgnoreCase(httpMethod) || "PATCH".equalsIgnoreCase(httpMethod)) {
            if (sale.getId() == null || !saleRepository.existsById(sale.getId())) {
                throw new ResourceNotFoundException(
                        "Update failed: No existing sale record found for the provided details."
                );
            }
        }
    }
}
