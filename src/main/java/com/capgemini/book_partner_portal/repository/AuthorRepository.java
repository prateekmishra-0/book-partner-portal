package com.capgemini.book_partner_portal.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.projection.AuthorListProjection;


@RepositoryRestResource(collectionResourceRel = "authors", path = "authors", excerptProjection = AuthorListProjection.class)
public interface AuthorRepository extends JpaRepository<Author, String> {

    @RestResource(path="firstname",rel="by-firstname")
    Page<Author> findByFirstNameContainingIgnoreCase(String firstName,Pageable pageable);
    
    @RestResource(path="lastname",rel="by-lastname")
    Page<Author> findByLastNameContainingIgnoreCase(String lastName,Pageable pageable);

    @RestResource(path="city",rel="by-city")
    Page<Author> findByCityStartingWithIgnoreCase(String city,Pageable pageable);

    @RestResource(path="phone",rel="by-phone")
    Page<Author> findByPhoneStartingWith(String phone,Pageable pageable);

    @RestResource(path="state",rel="by-state")
    Page<Author> findByStateStartingWithIgnoreCase(String state,Pageable pageable);

    @RestResource(path="zip",rel="by-zip")
    Page<Author> findByZipStartingWithIgnoreCase(String zip,Pageable pageable);


    
}