package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stores") // Matches the DB table name
@Data
public class Store {
    @Id
    @Column(name = "stor_id", length = 4, columnDefinition = "char(4)")
    private String storId;

    @Column(name = "stor_name", length = 40)
    private String storName;

    @Column(name = "stor_address", length = 40)
    private String storAddress;

    @Column(name = "city", length = 20)
    private String city;

    @Column(name = "state", length = 2, columnDefinition = "char(2)")
    private String state;

    @Column(name = "zip", length = 5, columnDefinition = "char(5)")
    private String zip;
}
