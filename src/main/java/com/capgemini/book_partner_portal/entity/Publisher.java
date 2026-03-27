package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "publishers")
// 1. Hijack the DELETE command and turn it into an UPDATE
@SQLDelete(sql = "UPDATE publishers SET is_active = false WHERE pub_id = ?")
// 2. Secretly append "WHERE is_active = true" to every single SELECT query
@SQLRestriction("is_active = true")
public class Publisher {

    @Id
    @Column(name = "pub_id", length = 4)
    // Constraint: 1389, 0736, 0877, 1622, 1756 OR 99XX pattern
    @Pattern(regexp = "^(1389|0736|0877|1622|1756|99\\d{2})$",
            message = "Invalid pub_id format")
    private String pubId;

    @NotBlank(message = "publisher name cannot be empty")
    @Column(name = "pub_name", length = 40)
    private String pubName;

    @Column(name = "city", length = 20)
    private String city;

    @Column(name = "state", length = 2)
    @Size(min = 2, max = 2, message = "state code must be 2 characters")
    private String state;

    @Builder.Default
    @Column(name = "country", length = 30)
    private String country = "USA";

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;
}
