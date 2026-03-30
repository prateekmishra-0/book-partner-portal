package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "titleauthor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TitleAuthor {

    @EmbeddedId
    private TitleAuthorId id;

    // --- READ ONLY SHIELDS ---
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "au_id", insertable = false, updatable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id", insertable = false, updatable = false)
    private Title title;

    // --- PAYLOAD COLUMNS ---

    @Column(name = "au_ord")
    @Min(value = 1, message = "Author order must be at least 1")
    private Byte auOrd;

    @Column(name = "royaltyper")
    @Min(value = 0, message = "Royalty percentage cannot be negative")
    @Max(value = 100, message = "Royalty percentage cannot exceed 100")
    private Integer royaltyPer;
}