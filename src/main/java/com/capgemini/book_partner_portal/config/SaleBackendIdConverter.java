package com.capgemini.book_partner_portal.config;

import com.capgemini.book_partner_portal.entity.Sale;
import com.capgemini.book_partner_portal.entity.SaleId;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Translator for Sale Composite Key.
 * This tells Spring Data REST how to read and write comma-separated URLs
 * (e.g., /api/sales/7131,ORD123,BU1032) into the SaleId Java object.
 */
@Component
public class SaleBackendIdConverter implements BackendIdConverter {

    /**
     * Translates the String from the URL into a SaleId object.
     */
    @Override
    public Serializable fromRequestId(String id, Class<?> entityType) {
        if (id == null) return null;

        String[] parts = id.split(",");
        if (parts.length == 3) {
            return new SaleId(parts[0], parts[1], parts[2]);
        }
        throw new IllegalArgumentException("Invalid Sale composite key format. Expected 'storId,ordNum,titleId'");
    }

    /**
     * Translates the SaleId object back into a String for JSON _links.
     */
    @Override
    public String toRequestId(Serializable id, Class<?> entityType) {
        SaleId saleId = (SaleId) id;
        return saleId.getStorId() + "," + saleId.getOrdNum() + "," + saleId.getTitleId();
    }

    /**
     * Tells Spring to only use this converter for the 'Sale' entity.
     */
    @Override
    public boolean supports(Class<?> delimiter) {
        return Sale.class.equals(delimiter);
    }
}
