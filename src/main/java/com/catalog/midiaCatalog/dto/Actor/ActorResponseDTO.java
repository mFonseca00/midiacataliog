package com.catalog.midiacatalog.dto.Actor;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorResponseDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
}
