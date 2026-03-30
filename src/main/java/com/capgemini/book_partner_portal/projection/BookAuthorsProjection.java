package com.capgemini.book_partner_portal.projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.capgemini.book_partner_portal.entity.TitleAuthor;

@Projection(name = "titleAuthors", types = { TitleAuthor.class })
public interface BookAuthorsProjection {

    Integer getRoyaltyPer();
    Byte getAuOrd();


    @Value("#{target.author.firstName}")
    String getFirstName();

    @Value("#{target.author.lastName}")
    String getLastName();

    @Value("#{target.author.city}")
    String getCity();

    @Value("#{target.author.state}")
    String getState();
}