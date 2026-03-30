package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Sale;
import com.capgemini.book_partner_portal.entity.SaleId;
import com.capgemini.book_partner_portal.projection.SaleDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Endpoint: /api/sales
 */
@RepositoryRestResource(
        collectionResourceRel = "sales",
        path = "sales",
        excerptProjection = SaleDetailProjection.class // Applies the math calculations by default
)
public interface SaleRepository extends JpaRepository<Sale, SaleId> {

    @RestResource(path = "byStore", rel = "by-store")
    List<Sale> findById_StorId(@Param("storId") String storId);
}
