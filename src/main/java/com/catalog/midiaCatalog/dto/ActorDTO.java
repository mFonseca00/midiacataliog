package com.catalog.midiacatalog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.catalog.midiacatalog.model.Midia;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO {
    private Long id;
    @NotBlank
    private String name;
    @Past
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private List<Midia> midias = new ArrayList<>();

    public ActorDTO(String name, LocalDate birth){
        this.name = name;
        this.birthDate = birth;
    }
}
