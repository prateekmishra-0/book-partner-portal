package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"store", "title"}) // Prevents lazy-loading exceptions in logging
public class Sale {

    @EmbeddedId
    private SaleId id;

    // --- READ ONLY SHIELDS ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stor_id", insertable = false, updatable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id", insertable = false, updatable = false)
    private Title title;

    // --- PAYLOAD COLUMNS ---

    @Column(name = "ord_date", nullable = false)
    @NotNull(message = "Order date is required")
    private LocalDateTime ordDate;

    /**
     * Quantity of books sold in this specific order line item.
     * Mapped to smallint in DB, so Short is the correct Java wrapper.
     */
    @Column(name = "qty", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Short qty;

    @Column(name = "payterms", length = 12, nullable = false)
    @NotBlank(message = "Payment terms are required")
    private String payterms;
}
