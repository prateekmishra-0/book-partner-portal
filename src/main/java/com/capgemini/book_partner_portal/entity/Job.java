package com.capgemini.book_partner_portal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for the 'jobs' table. Dev 5 (YOU) owns this.
 *
 * ARCHITECTURAL RULE ENFORCED: NO SOFT DELETES
 * Claude's review correctly noted that the jobs table has no is_active column
 * in the schema. Therefore, no @SQLDelete, @SQLRestriction, or @JsonIgnore
 * is applied here.
 */
@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing short
    @Column(name = "job_id", updatable = false)
    private Short jobId;

    @Column(name = "job_desc", length = 50, nullable = false)
    @NotBlank(message = "Job description is required")
    @Size(max = 50, message = "Job description must not exceed 50 characters")
    private String jobDesc = "New Position - title not formalized yet";

    @Column(name = "min_lvl", nullable = false)
    @Min(value = 10, message = "Minimum level must be at least 10")
    private Integer minLvl;

    @Column(name = "max_lvl", nullable = false)
    @Max(value = 250, message = "Maximum level cannot exceed 250")
    private Integer maxLvl;
}