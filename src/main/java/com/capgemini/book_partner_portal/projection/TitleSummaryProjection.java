package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Title;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import java.time.LocalDateTime;

@Projection(name = "titleSummary", types = { Title.class })
public interface TitleSummaryProjection {

    String getTitle();
    String getType();
    Double getPrice();
    Integer getRoyalty();
    Integer getYtdSales();
    String getNotes();
    LocalDateTime getPubdate();

    // Use a safer SpEL expression to ensure it doesn't break if publisher is null
    @Value("#{target.publisher != null ? target.publisher.pubName : 'N/A'}")
    String getPublisherName();

    @Value("#{target.publisher != null ? target.publisher.city : 'N/A'}")
    String getPublisherCity();
}