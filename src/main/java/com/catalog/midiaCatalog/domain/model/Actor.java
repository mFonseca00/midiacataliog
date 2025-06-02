package com.catalog.midiaCatalog.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actors")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Actor {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @ManyToMany(mappedBy = "actors")
    private List<Midia> midias = new ArrayList<>();
}
