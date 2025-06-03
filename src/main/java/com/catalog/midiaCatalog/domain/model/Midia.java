package com.catalog.midiaCatalog.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.catalog.midiaCatalog.domain.model.enums.Midiatype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midias")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Midia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type",nullable = false)
    private Midiatype type;

    @Column(name = "release_year")
    private Integer releaseYear;

    private String director;

    @Column(length = 5000)
    private String synopsis;

    private String genre;

    @Column(length = 1000, name = "poster_image_url")
    private String poseterImageUrl;

    @ManyToMany
    @JoinTable(
        name = "midia_actors",
        joinColumns = @JoinColumn(name = "midia_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors = new ArrayList<>();
}
