package com.catalog.midiacatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiacatalog.model.Actor;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long>{

}
