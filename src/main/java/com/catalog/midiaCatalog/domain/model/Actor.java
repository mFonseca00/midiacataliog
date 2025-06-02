package com.catalog.midiaCatalog.domain.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Actor {
    private Long id;
    private String name;
    private Date birthDate;
    private List<Midia> midias;
}
