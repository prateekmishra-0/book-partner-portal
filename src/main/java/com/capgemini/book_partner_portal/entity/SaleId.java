package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/*
 * CRITICAL LOMBOK ANNOTATION:
 * Hibernate requires @EqualsAndHashCode for all Composite Keys. Without this,
 * Hibernate compares the memory addresses of two SaleId objects instead of their
 * actual data, which causes database lookups to fail and throws severe caching errors.
 */
@EqualsAndHashCode
@ToString
public class SaleId implements Serializable {

    // Must implement Serializable so Java can convert this object into a byte stream
    // to travel across memory networks to the database.

    @Column(name = "stor_id", length = 4, nullable = false)
    private String storId;

    @Column(name = "ord_num", length = 20, nullable = false)
    private String ordNum;

    // Matches the corrected length=10 from Dev 1's Title entity
    @Column(name = "title_id", length = 10, nullable = false)
    private String titleId;
}
