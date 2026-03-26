package com.capgemini.book_partner_portal.repository;


import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.projection.PublisherSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers", excerptProjection = PublisherSummary.class)
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    @RestResource(path = "city", rel = "city-search")
    List<Publisher> findByCityIgnoreCase(@Param("city") String city);

    @RestResource(path = "pubname", rel = "name-search")
    Optional<Publisher> findByPubNameIgnoreCase(@Param("pubName") String pubName);

    @RestResource(path = "state", rel = "state-search")
    List<Publisher> findByStateIgnoreCase(@Param("state") String state);
}
