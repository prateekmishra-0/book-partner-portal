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

    /**
     * For Dev 3's Page 3 (Store Details).
     * Fetches every sale transaction associated with a specific Store ID.
     *
     * The "_" syntax tells Spring Data to look inside the @EmbeddedId composite key
     * and filter by the 'storId' field.
     *
     * Endpoint: GET /api/sales/search/byStore?storId=7131
     */
    @RestResource(path = "byStore", rel = "by-store")
    List<Sale> findById_StorId(@Param("storId") String storId);
}
