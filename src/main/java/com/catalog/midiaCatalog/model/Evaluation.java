package com.catalog.midiacatalog.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluations")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "midia_id", nullable = false)
    private Midia midia;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    @Min(value = 1, message = "Rating must be at least 1 stars")
    @Max(value = 5, message = "Rating must be at most 5 stars")
    private Integer rating;

    
    @Column(length = 1000)
    private String comment;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;
}
