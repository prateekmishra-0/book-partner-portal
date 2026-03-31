package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.projection.PublisherSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers", excerptProjection = PublisherSummaryProjection.class)
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    @RestResource(path = "city", rel = "city-search")
    Page<Publisher> findByCityContainingIgnoreCase(@Param("city") String city, Pageable pageable);

    @RestResource(path = "exact-pubname", rel = "exact-name-search")
    Optional<Publisher> findByPubName(@Param("pubName") String pubName);

    @RestResource(path = "pubname", rel = "name-search")
    Page<Publisher> findByPubNameContainingIgnoreCase(@Param("pubName") String pubName, Pageable pageable);

    @RestResource(path = "state", rel = "state-search")
    Page<Publisher> findByStateContainingIgnoreCase(@Param("state") String state, Pageable pageable);

    @RestResource(path = "country", rel = "country-search")
    Page<Publisher> findByCountryContainingIgnoreCase(@Param("country") String country, Pageable pageable);
}