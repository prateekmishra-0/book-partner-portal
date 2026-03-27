package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @Column(length = 12)
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
}