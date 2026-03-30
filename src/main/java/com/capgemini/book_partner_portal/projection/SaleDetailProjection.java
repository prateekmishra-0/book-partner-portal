package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Sale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDateTime;

/**
 * Detailed projection for the 'sales' transaction resource.
 */
@Projection(name = "saleDetail", types = {Sale.class})
public interface SaleDetailProjection {

    // 1. Flatten the Composite Key:
    // The frontend doesn't want to parse a nested 'id' object just to show
    // the Order Number. This SpEL trick extracts it directly to the top level.
    @Value("#{target.id.ordNum}")
    String getOrdNum();

    LocalDateTime getOrdDate();
    Short getQty();
    String getPayterms();

    // 2. The Firewall to Dev 1's Data:
    // Dev 3 needs the book's title and price, but absolutely nothing else.
    // This nested view prevents Dev 1's internal fields from leaking here.
    TitleView getTitle();

    interface TitleView {
        String getTitle();
        Double getPrice();
    }

    // 3. THE MAGIC: Dynamic Line Total Calculation
    // Multiplies the sale quantity by the associated book's price in real-time.
    // Null-safe checks prevent 500 errors if a book was physically deleted.
    @Value("#{target.qty * (target.title != null && target.title.price != null ? target.title.price : 0.0)}")
    Double getTotalAmount();
}
