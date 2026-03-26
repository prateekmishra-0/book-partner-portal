package com.capgemini.book_partner_portal.repository;


import com.capgemini.book_partner_portal.entity.Title;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.web.bind.annotation.RequestParam;


@RepositoryRestResource
public interface TitleRepository extends JpaRepository<Title, String> {
        
        List<Title> findByTitleContainingIgnoreCase(@RequestParam("title")String title);

        List<Title> findByType(@RequestParam("type")String type);

        Optional<Title> findByTitle(@RequestParam("title")String title);
  
}