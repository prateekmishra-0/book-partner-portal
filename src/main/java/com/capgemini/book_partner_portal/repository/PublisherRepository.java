package com.capgemini.book_partner_portal.repository;


import com.capgemini.book_partner_portal.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    @RestResource(path = "city", rel = "city-search")
    Optional<List<Publisher>> findByCity(@Param("city") String city);
}
