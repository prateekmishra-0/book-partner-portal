package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "stores") // Matches the DB table name
@Data
@SQLDelete(sql = "UPDATE stores SET is_active = false WHERE stor_id = ?")
@SQLRestriction("is_active = true")
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @NotNull(message = "Store ID is required")
    @Size(min = 4, max = 4,message = "Store ID must be exactly 4 characters")
    @Column(name = "stor_id",nullable=false, length = 4, columnDefinition = "char(4)", updatable = false)
    private String storId;

    @Size(max = 40, message = "Store name must not exceed 40 characters")
    @Column(name = "stor_name", length = 40)
    private String storName;

    @Column(name = "stor_address", length = 40)
    @Size(max = 40, message = "Store address must not exceed 40 characters")
    private String storAddress;

    @Column(name = "city", length = 20)
    @Size(max = 20, message = "City must not exceed 20 characters")
    private String city;

    @Size(max = 2)
    @Column(name = "state", length = 2, columnDefinition = "char(2)")
    private String state;

    @Column(name = "zip", columnDefinition = "CHAR(5)")
    @Pattern(regexp = "^[0-9]{5}$", message = "Zip code must be exactly 5 digits")
    private String zip;

    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


}
