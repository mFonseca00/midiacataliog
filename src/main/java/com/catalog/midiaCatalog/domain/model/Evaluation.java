package com.catalog.midiaCatalog.domain.model;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Evaluation {
    private Long id;
    private Midia midia;
    private User user;
    private Integer rating;
    private String comment;
    private Date evaluationDate;
}
