package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "titles", path = "titles")
public interface TitleRepository extends JpaRepository<Title, String> {

    // --- Title Name Searches ---
    Optional<Title> findByTitle(@Param("title") String title);
    
    List<Title> findByTitleContainingIgnoreCase(@Param("title") String title);

    // --- Type/Genre Searches ---
    List<Title> findByTypeIgnoreCase(@Param("type") String type);

    // --- Price Comparison Searches (Matching Employee Job Level logic) ---
    List<Title> findByPrice(@Param("price") Double price);
    
    List<Title> findByPriceGreaterThan(@Param("price") Double price);
    
    List<Title> findByPriceLessThan(@Param("price") Double price);
    
    List<Title> findByPriceBetween(@Param("min") Double min, @Param("max") Double max);
}