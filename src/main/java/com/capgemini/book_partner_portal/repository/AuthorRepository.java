package com.capgemini.book_partner_portal.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.capgemini.book_partner_portal.entity.Author;

@RepositoryRestResource
public interface AuthorRepository extends JpaRepository<Author, String> {

}