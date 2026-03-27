package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository extends JpaRepository<Store,String> {
    @RestResource(path = "city")
    List<Store> findByCityIgnoreCase(@Param("city") String city);

    // State search (Case Insensitive)
    @RestResource(path = "state")
    List<Store> findByStateIgnoreCase(@Param("state") String state);

    // Store Name search (Partial match + Case Insensitive)
    @RestResource(path = "name")
    List<Store> findByStorNameContainingIgnoreCase(@Param("name") String name);


}
