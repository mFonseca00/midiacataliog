package com.catalog.midiaCatalog.domain.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Actor {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthDate;

    private List<Midia> midias;
}
