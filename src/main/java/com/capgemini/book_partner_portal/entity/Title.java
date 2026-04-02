package com.capgemini.book_partner_portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity for the 'titles' table.
 * Dev 1 is the SOLE OWNER of this file.
 *
 * KEY ARCHITECTURAL DECISIONS:
 *
 * 1. DUAL pub_id PATTERN (The Read-Only Shield):
 *    pub_id is declared TWICE:
 *      a) As a plain @Column string (pubId) — this is what the client sends
 *         in the JSON body to link/change a publisher. Hibernate writes this.
 *      b) As a @ManyToOne with the shield (insertable=false, updatable=false)
 *         — this is what Hibernate JOINs for reading publisher details.
 *    Dev 4 owns Publisher.java. The shield on the @ManyToOne guarantees Dev 1
 *    can NEVER accidentally mutate a publisher record through this relationship.
 *
 * 2. FetchType.LAZY on publisher:
 *    The original entity used EAGER, which forces a JOIN on every single
 *    title query even when publisher details are not needed (e.g., a simple
 *    price lookup). LAZY loads the publisher only when the field is accessed,
 *    which is the correct JPA performance default for @ManyToOne.
 *
 * 3. title_id length corrected from 6 → 10:
 *    The original entity had length=6. The DB schema defines title_id as
 *    varchar(10). This mismatch would cause silent truncation for IDs longer
 *    than 6 characters. Fixed to match the DB exactly.
 *
 * 4. pubdate is updatable=false:
 *    Requirement: "Publication Date (pubdate) is locked and cannot be changed."
 *    Enforcing this at the @Column level means Hibernate silently ignores any
 *    pubdate value in a PUT/PATCH body — no event handler logic required.
 *
 * 5. @JsonIgnore on isActive:
 *    Clients must never see or send this field. It is controlled entirely
 *    by @SQLDelete (soft delete hook) and TitleEventHandler (create bouncer).
 *    Without @JsonIgnore, a client could observe the is_active state and
 *    attempt to manipulate it directly.
 *
 * 6. @SQLRestriction("is_active = true"):
 *    Every findAll(), findById(), and search query automatically appends
 *    WHERE is_active = true. Soft-deleted titles are invisible at the
 *    Hibernate level — no manual filtering needed anywhere in repositories.
 */
@Entity
@Table(name = "titles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE titles SET is_active = false WHERE title_id = ?")
//@SQLRestriction("is_active = true")
public class Title {

    /**
     * Primary key — title_id.
     * updatable=false: once set, cannot be changed via PUT/PATCH.
     * length=10: matches DB varchar(10) — was incorrectly 6 in the original.
     */
    @Id
    @Column(name = "title_id", nullable = false, length = 10, updatable = false)
    @NotBlank(message = "Title ID is required")
    private String titleId;

    @Column(name = "title", length = 80, nullable = false)
    @NotBlank(message = "Title name is required")
    @Size(max = 80, message = "Title name must not exceed 80 characters")
    private String title;

    /**
     * Book genre/category.
     * DB default: 'UNDECIDED' — initialized here to match.
     * columnDefinition="CHAR(12)": DB uses fixed-length CHAR, not VARCHAR.
     */
    @Column(name = "type", columnDefinition = "CHAR(12)", nullable = false)
    @NotBlank(message = "Type is required")
    @Size(max = 12, message = "Type must not exceed 12 characters")
    private String type = "UNDECIDED";

    // -------------------------------------------------------------------------
    // PUBLISHER LINK — Dual Field Pattern with Read-Only Shield
    // -------------------------------------------------------------------------

    /**
     * The raw FK value written to the pub_id column.
     * Client sends this in the JSON body: { "pubId": "1389" }
     * Hibernate writes this value directly to the DB column.
     */
    @Column(name = "pub_id", length = 4)
    private String pubId;

    /**
     * Read-only navigation object — populated by Hibernate via JOIN.
     *
     * THE SHIELD: insertable=false, updatable=false
     * This @ManyToOne NEVER writes to the pub_id column.
     * Only the plain pubId String field above controls that column.
     * This protects Dev 4's Publisher entity from accidental mutation.
     *
     * FetchType.LAZY: publisher details loaded only when accessed,
     * not on every title query. Critical for list-view performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pub_id", insertable = false, updatable = false)
    private Publisher publisher;

    // -------------------------------------------------------------------------
    // Core Fields
    // -------------------------------------------------------------------------

    @Column(name = "price")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;

    @Column(name = "advance")
    @DecimalMin(value = "0.0", message = "Advance cannot be negative")
    private Double advance;

    @Column(name = "royalty")
    @Min(value = 0, message = "Royalty must be between 0 and 100")
    @Max(value = 100, message = "Royalty must be between 0 and 100")
    private Integer royalty;

    @Column(name = "ytd_sales")
    @Min(value = 0, message = "Year-to-date sales cannot be negative")
    private Integer ytdSales;

    @Column(name = "notes", length = 200)
    @Size(max = 200, message = "Notes must not exceed 200 characters")
    private String notes;

    /**
     * Publication date — immutable after creation.
     * updatable=false enforces: "Publication Date is locked and cannot be changed."
     * Any pubdate value in a PUT/PATCH body is silently ignored by Hibernate.
     */
    @Column(name = "pubdate", nullable = false, updatable = false)
    private LocalDateTime pubdate;

    

    /**
     * Soft delete flag.
     * @JsonIgnore: clients never see or send this field.
     * Controlled by @SQLDelete (on delete) and TitleEventHandler (on create).
     */
    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

 
}
