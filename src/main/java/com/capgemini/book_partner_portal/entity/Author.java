package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String auId;

    @Column(name = "au_lname", nullable = false)
    private String lastName;

    @Column(name = "au_fname", nullable = false)
    private String firstName;

    @Column(name = "phone", length = 12)
    private String phone = "UNKNOWN";

    private String address;
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 5)
    private String zip;

    @Column(nullable = false)
    private Integer contract;
}