package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "stores") // Matches the DB table name
@Data
@SQLDelete(sql = "UPDATE stores SET is_active = false WHERE stor_id = ?") // Yahan bhi false kar sakte ho
@SQLRestriction("is_active = true")
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @NotNull(message = "Store ID is required")
    @Size(min = 4, max = 4)
    @Column(name = "stor_id", length = 4, columnDefinition = "char(4)")
    private String storId;

    @Size(max = 40)
    @Column(name = "stor_name", length = 40)
    private String storName;

    @Column(name = "stor_address", length = 40)
    private String storAddress;

    @Column(name = "city", length = 20)
    private String city;

    @Size(max = 2)
    @Column(name = "state", length = 2, columnDefinition = "char(2)")
    private String state;

    @Size(max = 5)
    @Column(name = "zip", length = 5, columnDefinition = "char(5)")
    private String zip;

    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
