package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "publishers")
public class Publisher {

    @Id
    @Column(name = "pub_id", length = 4)
    // Constraint: 1389, 0736, 0877, 1622, 1756 OR 99XX pattern
    @Pattern(regexp = "^(1389|0736|0877|1622|1756|99\\d{2})$",
            message = "Invalid pub_id format")
    private String pubId;

    @Column(name = "pub_name", length = 40)
    private String pubName;

    @Column(name = "city", length = 20)
    private String city;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "country", length = 30)
    private String country = "USA";
}
