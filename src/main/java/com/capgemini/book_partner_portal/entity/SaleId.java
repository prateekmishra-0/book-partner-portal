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
@EqualsAndHashCode
@ToString
public class SaleId implements Serializable {

    @Column(name = "stor_id", length = 4, nullable = false)
    private String storId;

    @Column(name = "ord_num", length = 20, nullable = false)
    private String ordNum;

    // Matches the corrected length=10 from Dev 1's Title entity
    @Column(name = "title_id", length = 10, nullable = false)
    private String titleId;
}
