package com.catalog.midiaCatalog.domain.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Evaluation {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private Midia midia;

    @NotBlank
    private User user;

    @NotBlank
    private Integer rating;

    private String comment;

    private Date evaluationDate;
}
