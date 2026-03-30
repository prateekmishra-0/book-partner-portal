package com.capgemini.book_partner_portal.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.projection.AuthorListProjection;


@RepositoryRestResource(collectionResourceRel = "authors", path = "authors", excerptProjection = AuthorListProjection.class)
public interface AuthorRepository extends JpaRepository<Author, String> {

    @RestResource(path="firstname",rel="by-firstname")
    List<Author> findByFirstNameContainingIgnoreCase(String firstName);
    
    @RestResource(path="lastname",rel="by-lastname")
    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    @RestResource(path="city",rel="by-city")
    List<Author> findByCityIgnoreCase(String city);

    @RestResource(path="phone",rel="by-phone")
    List<Author> findByPhone(String phone);

    @RestResource(path="state",rel="by-state")
    List<Author> findByStateIgnoreCase(String state);

    @RestResource(path="zip",rel="by-zip")
    List<Author> findByZipIgnoreCase(String zip);


    
}