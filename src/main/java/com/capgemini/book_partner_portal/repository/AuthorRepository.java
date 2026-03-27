package com.capgemini.book_partner_portal.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.capgemini.book_partner_portal.entity.Author;

@RepositoryRestResource(path="authors")
public interface AuthorRepository extends JpaRepository<Author, String> {

    @RestResource(path="firstname")
    List<Author> findByFirstNameContainingIgnoreCase(String firstName);
    
    @RestResource(path="lastname")
    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    @RestResource(path="city")
    List<Author> findByCityIgnoreCase(String city);

    @RestResource(path="phone")
    List<Author> findByPhone(String phone);

    @RestResource(path="state")
    List<Author> findByStateIgnoreCase(String state);


    
}