package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite Primary Key for the 'titleauthor' join table.
 * Must implement Serializable and have Equals/HashCode for JPA caching.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TitleAuthorId implements Serializable {

    @Column(name = "au_id", length = 11, nullable = false)
    private String auId;

    @Column(name = "title_id", length = 10, nullable = false)
    private String titleId;
}