package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Store;
import org.springframework.data.rest.core.config.Projection;

/**
 * Shared projection for the 'stores' resource.
 * Dev 3 is the SOLE OWNER of this file.
 *
 * HATEOAS COMPLIANCE:
 * As established in Dev 1 and Dev 2, we omit getStorId() here. The UI
 * extracts the ID from the _links.self.href property.
 */
@Projection(name = "storeSummary", types = {Store.class})
public interface StoreSummaryProjection {

    String getStorName();
    String getStorAddress();
    String getCity();
    String getState();
    String getZip();

}
