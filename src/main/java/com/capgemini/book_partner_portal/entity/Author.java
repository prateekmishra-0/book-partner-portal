package com.capgemini.book_partner_portal.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
// 1. Hijack the DELETE command and turn it into an UPDATE
@SQLDelete(sql = "UPDATE authors SET is_active = false WHERE au_id = ?")
// 2. Secretly append "WHERE is_active = true" to every single SELECT query
@SQLRestriction("is_active = true")
public class Author {

    @Id
    @Column(name = "au_id", length = 11)
    @NotBlank(message = "Author ID is required")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{4}$", message = "Author ID must be in format XXX-XX-XXXX")
    private String auId;

    @Column(name = "au_lname", nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(name = "au_fname", nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Builder.Default
    @Column(nullable = false, length = 12)
    private String phone = "UNKNOWN";

    private String address;
    private String city;

    @Column(length = 2)
    @Size(max = 2, message = "State must be 2 characters")
    private String state;

    @Column(length = 5)
    @Pattern(regexp = "^[0-9]{5}$", message = "ZIP must be 5 digits")
    private String zip;

    @NotNull(message = "Contract is required")
    @Column(nullable = false)
    private Integer contract;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


}