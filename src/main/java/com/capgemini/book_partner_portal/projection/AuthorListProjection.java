package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Author;
import org.springframework.data.rest.core.config.Projection;


@Projection(name = "authorList", types = {Author.class})
public interface AuthorListProjection {

    // String getAuId();
    String getFirstName();
    String getLastName();
    String getPhone();
    String getCity();
    String getState();
    

}