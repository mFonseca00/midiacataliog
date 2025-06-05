package com.catalog.midiacatalog.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorRegistratioDTO {
    @NotBlank
    private String name;
    @Past
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

}
