package com.capgemini.book_partner_portal.repository;


import com.capgemini.book_partner_portal.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PublisherRepository extends JpaRepository<Publisher, String> {


}
