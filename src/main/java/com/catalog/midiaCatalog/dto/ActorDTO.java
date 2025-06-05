package com.catalog.midiacatalog.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.catalog.midiacatalog.model.Midia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorDTO {
    @NotBlank
    private String name;
    @Past
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;
    private List<Midia> midias = new ArrayList<>();

    public ActorDTO(String name, Date birth){
        this.name = name;
        this.birthDate = birth;
    }
}
