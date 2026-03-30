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

    // Extracts 'ordNum' from the composite key for a flatter JSON response.
    @Value("#{target.id.ordNum}")
    String getOrdNum();

    LocalDateTime getOrdDate();
    Short getQty();
    String getPayterms();

    // Restricts exposed Title data to just 'title' and 'price'.
    TitleView getTitle();

    interface TitleView {
        String getTitle();
        Double getPrice();
    }

    // Calculates total revenue (qty * price) on the fly, safely handling nulls.
    @Value("#{target.qty * (target.title != null && target.title.price != null ? target.title.price : 0.0)}")
    Double getTotalAmount();
}
