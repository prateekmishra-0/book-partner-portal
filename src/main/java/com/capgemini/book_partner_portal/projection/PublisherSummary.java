package com.capgemini.book_partner_portal.projection;

import com.capgemini.book_partner_portal.entity.Publisher;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "publisherSummary", types = {Publisher.class})
public interface PublisherSummary {

    String getPubName();

    String getCity();

    String getCountry();

    String getState();
}
