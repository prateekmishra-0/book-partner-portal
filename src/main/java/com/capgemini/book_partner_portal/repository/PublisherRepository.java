package com.capgemini.book_partner_portal.repository;


import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.projection.PublisherSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers", excerptProjection = PublisherSummaryProjection.class)
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    @RestResource(path = "city", rel = "city-search")
    List<Publisher> findByCityContainingIgnoreCase(@Param("city") String city);

    @RestResource(path = "exact-pubname", rel = "exact-name-search")
    Optional<Publisher> findByPubName(@Param("pubName") String pubName);

    @RestResource(path = "pubname", rel = "name-search")
    List<Publisher> findByPubNameContainingIgnoreCase(@Param("pubName") String pubName);

    @RestResource(path = "state", rel = "state-search")
    List<Publisher> findByStateContainingIgnoreCase(@Param("state") String state);

    @RestResource(path = "country", rel = "country-search")
    List<Publisher> findByCountryContainingIgnoreCase(@Param("country") String country);
}
