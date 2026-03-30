package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.projection.TitleSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the 'titles' table.
 * Dev 1 owns this file.
 * Dev 4 DEPENDS on findByPubId — do not rename or remove that method.
 *
 * CHANGES FROM THE ORIGINAL CODEBASE:
 *
 * 1. excerptProjection = TitleSummaryProjection.class:
 *    Forces every collection response (GET /api/titles) to use the projection
 *    mask automatically. Without this, the full Title entity including all fields
 *    is serialized, triggering lazy-loads and risking field exposure.
 *    Individual resource responses (GET /api/titles/{id}) still return the full
 *    entity — the projection only applies to the embedded list view.
 *
 * 2. @RestResource(path = ...) on ALL methods:
 *    Without explicit path values, Spring Data REST uses the full Java method
 *    name as the URL segment — ugly and fragile under refactoring.
 *    With explicit paths, the URL is stable regardless of method renaming.
 *
 * 3. @Param on ALL parameters:
 *    Required for production builds compiled without debug symbols.
 *    Without @Param, Spring Data REST cannot reliably bind query parameters
 *    to method arguments in JARs compiled with -g:none (standard in CI/CD).
 *
 * 4. findByPubId() at path "publisher":
 *    Dev 4's Page 3 requirement: GET /api/titles/search/publisher?pubId=0736
 *    @SQLRestriction on the Title entity automatically filters soft-deleted
 *    titles — no manual WHERE clause needed in this method.
 *
 * EXPOSED SEARCH ENDPOINTS (all under /api/titles/search/):
 *   GET /api/titles/search/exact?title=...          → exact title name match
 *   GET /api/titles/search/name?title=...           → partial match on title name
 *   GET /api/titles/search/type?type=...            → by genre/type
 *   GET /api/titles/search/price-exact?price=...    → exact price
 *   GET /api/titles/search/price-gt?price=...       → price greater than
 *   GET /api/titles/search/price-lt?price=...       → price less than
 *   GET /api/titles/search/price-range?min=&max=    → price between range
 *   GET /api/titles/search/publisher?pubId=...      → all titles for a publisher
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

    @RestResource(path = "name", rel = "title-search")
    List<Title> findByTitleContainingIgnoreCase(@Param("title") String title);

    // --- Type / Genre Searches ---

    @RestResource(path = "type", rel = "type-search")
    List<Title> findByTypeIgnoreCase(@Param("type") String type);

    // --- Price Comparison Searches ---

    @RestResource(path = "price-exact", rel = "price-exact")
    List<Title> findByPrice(@Param("price") Double price);

    @RestResource(path = "price-gt", rel = "price-gt")
    List<Title> findByPriceGreaterThan(@Param("price") Double price);

    @RestResource(path = "price-lt", rel = "price-lt")
    List<Title> findByPriceLessThan(@Param("price") Double price);

    @RestResource(path = "price-range", rel = "price-range")
    List<Title> findByPriceBetween(@Param("min") Double min, @Param("max") Double max);

    // --- Publisher Search (DEV 4 DEPENDENCY — do not rename or remove) ---

    /**
     * Returns all active titles belonging to a given publisher.
     * @SQLRestriction on Title entity handles soft-delete filtering automatically.
     *
     * Endpoint: GET /api/titles/search/publisher?pubId=0736
     * Consumer: Dev 4 — Page 3 (Titles by Publisher)
     *
     * Contract: path = "publisher" and @Param("pubId") must not change.
     * Dev 4 has hardcoded this URL in their frontend. Any change here requires
     * coordination with Dev 4 first.
     */
    @RestResource(path = "publisher", rel = "by-publisher")
    List<Title> findByPubId(@Param("pubId") String pubId);
}
