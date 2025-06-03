package com.catalog.midiaCatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiaCatalog.model.Actor;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long>{

}
