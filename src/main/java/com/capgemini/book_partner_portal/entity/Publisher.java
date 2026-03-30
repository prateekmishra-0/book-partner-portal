package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entity for the 'publishers' table.
 * Dev 4 is the SOLE OWNER of this file.
 *
 * ARCHITECTURAL RULES ENFORCED:
 * 1. ZERO BI-DIRECTIONAL RELATIONSHIPS:
 * There are NO @OneToMany lists of Titles or Employees here.
 * If Dev 4 needs a list of a publisher's books, the UI will call Dev 1's
 * endpoint: /api/titles/search/publisher?pubId=...
 *
 * 2. SOFT DELETE SECURITY:
 * @SQLDelete and @SQLRestriction handle the persistence filtering automatically.
 * @JsonIgnore ensures the isActive flag never leaks to the frontend payload.
 */
@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE publishers SET is_active = false WHERE pub_id = ?")
@SQLRestriction("is_active = true")
@Builder
public class Publisher {

    /**
     * Matches DB: pub_id char(4) NOT NULL.
     * updatable = false prevents primary key tampering via PUT/PATCH.
     * The Regex perfectly matches the DB CHECK constraint:
     * (pub_id in ('1389', '0736', '0877', '1622', '1756') OR pub_id like '99%')
     */
    @Id
    @Column(name = "pub_id", nullable = false, length = 4, updatable = false)
    @NotBlank(message = "Publisher ID is required")
    @Pattern(regexp = "^(1389|0736|0877|1622|1756|99[0-9]{2})$", message = "Invalid Publisher ID format. Must be a legacy ID or start with 99.")
    private String pubId;

    @Column(name = "pub_name", length = 40)
    @Size(max = 40, message = "Publisher name must not exceed 40 characters")
    @NotBlank(message = "publisher name cannot be blank")
    private String pubName;

    @Column(name = "city", length = 20)
    @Size(max = 20, message = "City must not exceed 20 characters")
    private String city;

    @Column(name = "state", columnDefinition = "CHAR(2)")
    @Size(min = 2, max = 2, message = "state must be two letters")
    private String state;

    @Column(name = "country", length = 30)
    @Size(max = 30, message = "Country must not exceed 30 characters")
    @Builder.Default
    private String country = "USA";

    /**
     * Soft delete flag.
     * @JsonIgnore is MANDATORY so clients cannot see or manipulate this via API.
     */
    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}