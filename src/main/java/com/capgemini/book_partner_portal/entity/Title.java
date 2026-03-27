package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "titles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE titles SET is_active = false WHERE title_id = ?")
// 2. Secretly append "WHERE is_active = true" to every single SELECT query
@SQLRestriction("is_active = true")
public class Title {

    @Id
    @Column(name = "title_id", nullable = false, length = 6, updatable = false) 
    private String titleId;

    @Column(name = "title", length = 80, nullable = false)
    @NotBlank(message = "Title name is required")
    @Size(max = 80)
    private String title;

    @Column(name = "type", columnDefinition = "CHAR(12)", nullable = false)
    @NotBlank(message = "Type is required")
    @Size(max = 12)
    private String type = "UNDECIDED"; 
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pub_id", referencedColumnName = "pub_id")
    private Publisher publisher; 

    @Column(name = "price")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;

    @Column(name = "advance")
    private Double advance;

    @Column(name = "royalty")
    @Min(0)
    @Max(100)
    private Integer royalty;

    @Column(name = "ytd_sales")
    private Integer ytdSales;

    @Column(name = "notes", length = 200)
    @Size(max = 200)
    private String notes;

    @Column(name = "pubdate", nullable = false, updatable = false) 
    private LocalDateTime pubdate;

    @JsonIgnore // 
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}