package com.catalog.midiacatalog.dto.Midia;

import java.util.ArrayList;
import java.util.List;

import com.catalog.midiacatalog.model.enums.Midiatype;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MidiaRegistrationDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Midia type is required")
    private Midiatype type;

    private Integer releaseYear;

    private String director;

    private String synopsis;

    private String genre;

    private String poseterImageUrl;

    private List<Long> actorIds = new ArrayList<>();
}
