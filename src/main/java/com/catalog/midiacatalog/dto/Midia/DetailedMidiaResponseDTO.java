package com.catalog.midiacatalog.dto.Midia;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.catalog.midiacatalog.dto.Actor.ActorSimpleDTO;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.enums.Midiatype;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailedMidiaResponseDTO {

    private Long id;

    private String title;

    private Midiatype type;

    private Integer releaseYear;

    private String director;

    private String synopsis;

    private String genre;

    private String poseterImageUrl;
    private List<ActorSimpleDTO> actors = new ArrayList<>();

    public DetailedMidiaResponseDTO(Long id, String title, Midiatype type, Integer releaseYear,
                                    String director, String synopsis, String genre,
                                    String poseterImageUrl, List<Actor> actors) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.releaseYear = releaseYear;
        this.director = director;
        this.synopsis = synopsis;
        this.genre = genre;
        this.poseterImageUrl = poseterImageUrl;
        
        if (actors != null) {
            this.actors = actors.stream()
                .map(actor -> new ActorSimpleDTO(actor.getId(), actor.getName()))
                .collect(Collectors.toList());
        }
    }
}
