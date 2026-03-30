package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import com.capgemini.book_partner_portal.projection.StoreSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores", excerptProjection = StoreSummaryProjection.class)
public interface StoreRepository extends JpaRepository<Store,String> {

    // --- Page 2 Search Endpoints ---

    @RestResource(path = "name", rel = "by-name")
    List<Store> findByStorNameContainingIgnoreCase(@Param("name") String name);

    @RestResource(path = "city", rel = "by-city")
    List<Store> findByCityIgnoreCase(@Param("city") String city);

    @RestResource(path = "state", rel = "by-state")
    List<Store> findByStateIgnoreCase(@Param("state") String state);



}
