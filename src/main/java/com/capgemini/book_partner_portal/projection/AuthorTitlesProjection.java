package com.capgemini.book_partner_portal.projection;



import org.springframework.beans.factory.annotation.Value;

import com.capgemini.book_partner_portal.entity.TitleAuthor;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "authorTitles", types = { TitleAuthor.class })
public interface AuthorTitlesProjection {
    
    // Flat fields from the bridge table
    Integer getRoyaltyPer();
    Byte getAuOrd();

    // Flattening the Title data
    @Value("#{target.title.title}")
    String getBookTitle();

    @Value("#{target.title.price}")
    Double getPrice();

    @Value("#{target.title.publisher != null ? target.title.publisher.pubName : 'N/A'}")
    String getPublisherName();
}