package com.catalog.midiacatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiacatalog.model.Midia;

@Repository
public interface MidiaRepository extends JpaRepository<Midia, Long>{

}
