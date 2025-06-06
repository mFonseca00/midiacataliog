package com.catalog.midiacatalog.dto.Midia;

import java.util.ArrayList;
import java.util.List;

import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.enums.Midiatype;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MidiaDTO {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private Midiatype type;
    private Integer releaseYear;
    private String director;
    private String synopsis;
    private String genre;
    private String poseterImageUrl;
    private List<Actor> actors = new ArrayList<>();
}
