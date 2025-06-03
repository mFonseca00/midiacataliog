package com.catalog.midiaCatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiaCatalog.model.Midia;

@Repository
public interface MidiaRepository extends JpaRepository<Midia, Long>{

}
