package com.capgemini.book_partner_portal.projection;

import org.springframework.data.rest.core.config.Projection;

import com.capgemini.book_partner_portal.entity.Author;

@Projection(name = "authorList", types = {Author.class})
public interface AuthorListProjection {

    String getAuId();
    String getFirstName();
    String getLastName();
    String getCity();

}