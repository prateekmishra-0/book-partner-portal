package com.capgemini.book_partner_portal.config;

import com.capgemini.book_partner_portal.entity.TitleAuthor;
import com.capgemini.book_partner_portal.entity.TitleAuthorId;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;
import java.io.Serializable;

@Component
public class TitleAuthorIdConverter implements BackendIdConverter {

    @Override
    public boolean supports(Class<?> delimiter) {
        // Only apply this logic to the TitleAuthor entity
        return delimiter.equals(TitleAuthor.class);
    }

    @Override
    public Serializable fromRequestId(String id, Class<?> entityType) {
        if (id == null || !id.contains("_")) return null;
        
        // Splits "AU-ID_TITLE-ID" back into the two parts
        String[] parts = id.split("_");
        return new TitleAuthorId(parts[0], parts[1]);
    }

    @Override
    public String toRequestId(Serializable id, Class<?> entityType) {
        TitleAuthorId titleAuthorId = (TitleAuthorId) id;
        // Combines them into a clean URL string: "123-45-6789_BU1032"
        return titleAuthorId.getAuId() + "_" + titleAuthorId.getTitleId();
    }
}