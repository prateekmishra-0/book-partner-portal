package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.projection.TitleSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

/**
 * Refactored for Pagination Support.
 * All collection searches now return Page<Title> and accept Pageable.
 */
@RepositoryRestResource(
    collectionResourceRel = "titles",
    path = "titles",
    excerptProjection = TitleSummaryProjection.class
)
public interface TitleRepository extends JpaRepository<Title, String> {

    // --- Title Name Searches ---

    @RestResource(path = "exact", rel = "exact-title")
    Optional<Title> findByTitle(@Param("title") String title);

    // PAGINATED: Returns a slice of titles matching the partial name
    @RestResource(path = "name", rel = "title-search")
    Page<Title> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    // --- Type / Genre Searches ---

    // PAGINATED: Returns a slice of titles by genre
    @RestResource(path = "type", rel = "type-search")
    Page<Title> findByTypeIgnoreCase(@Param("type") String type, Pageable pageable);

    // --- Price Comparison Searches ---

    @RestResource(path = "price-exact", rel = "price-exact")
    Page<Title> findByPrice(@Param("price") Double price, Pageable pageable);

    @RestResource(path = "price-gt", rel = "price-gt")
    Page<Title> findByPriceGreaterThan(@Param("price") Double price, Pageable pageable);

    @RestResource(path = "price-lt", rel = "price-lt")
    Page<Title> findByPriceLessThan(@Param("price") Double price, Pageable pageable);

    @RestResource(path = "price-range", rel = "price-range")
    Page<Title> findByPriceBetween(@Param("min") Double min, @Param("max") Double max, Pageable pageable);

    // --- Publisher Search (DEV 4 DEPENDENCY) ---

    /**
     * PAGINATED: Returns a slice of active titles for a given publisher.
     * Updated to Page<Title> to handle large catalogs per publisher.
     */
    @RestResource(path = "publisher", rel = "by-publisher")
    Page<Title> findByPubId(@Param("pubId") String pubId, Pageable pageable);

    @Override
    @Query("SELECT t FROM Title t WHERE t.isActive = true")
    Page<Title> findAll(Pageable pageable);
}