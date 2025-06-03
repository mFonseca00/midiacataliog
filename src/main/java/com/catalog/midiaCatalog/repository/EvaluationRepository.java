package com.catalog.midiaCatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiaCatalog.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Long>{

}
