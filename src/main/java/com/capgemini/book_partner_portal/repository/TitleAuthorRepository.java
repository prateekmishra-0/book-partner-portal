package com.capgemini.book_partner_portal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.capgemini.book_partner_portal.entity.TitleAuthor;
import com.capgemini.book_partner_portal.entity.TitleAuthorId;
import com.capgemini.book_partner_portal.projection.AuthorTitlesProjection;



/**
 * Repository for the 'titleauthor' join table.
 * Endpoint: /api/titleAuthors
 */
@RepositoryRestResource(collectionResourceRel = "titleAuthors", path = "titleAuthors", excerptProjection = AuthorTitlesProjection.class)

public interface TitleAuthorRepository extends JpaRepository<TitleAuthor, TitleAuthorId> {

    /**
     * For Dev 1: Gets all authors linked to a specific book.
     * Endpoint: GET /api/titleAuthors/search/byTitle?titleId=BU1032
     * Spring Data translates the "_" to navigate inside the @EmbeddedId.
     */
    @RestResource(path = "byTitle", rel = "by-title")
    Page<TitleAuthor> findById_TitleId(@Param("titleId") String titleId,Pageable pageable);

    /**
     * For Dev 2: Gets all books linked to a specific author.
     * Endpoint: GET /api/titleAuthors/search/byAuthor?auId=172-32-1176
     */
    @RestResource(path = "byAuthor", rel = "by-author")
    Page<TitleAuthor> findById_AuId(@Param("auId") String auId,Pageable pageable);
}