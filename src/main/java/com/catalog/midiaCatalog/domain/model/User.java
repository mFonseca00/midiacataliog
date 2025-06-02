package com.catalog.midiaCatalog.domain.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
}
