package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository extends JpaRepository<Store,String> {

}
