package com.catalog.midiacatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiacatalog.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Long>{

}
