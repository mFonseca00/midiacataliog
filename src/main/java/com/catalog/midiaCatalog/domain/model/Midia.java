package com.catalog.midiaCatalog.domain.model;

import java.util.List;

import com.catalog.midiaCatalog.domain.model.enums.Midiatype;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Midia {
    
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private Midiatype type;

    private Integer releaseYear;

    private String director;

    private String synopsis;

    private String genre;

    private String poseterImageUrl;

    private List<Actor> actors;
}
