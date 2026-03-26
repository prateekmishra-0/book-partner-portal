package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository extends JpaRepository<Store,String> {
    List<Store> findByCity(@Param("city") String city);
    List<Store> findByState(@Param("state") String state);
    List<Store> findByStorNameContaining(@Param("name") String name);


}
