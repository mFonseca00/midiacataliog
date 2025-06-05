package com.catalog.midiacatalog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.ActorDTO;
import com.catalog.midiacatalog.dto.ActorRegistratioDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.repository.ActorRepository;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    public ActorRegistratioDTO register(ActorRegistratioDTO actorDTO) {
        if(actorDTO.getName() == null || actorDTO.getName() == "")
            throw new DataValidationException("Actor name must be informed.");
        
        if(actorDTO.getBirthDate().isAfter(java.time.LocalDate.now())) 
            throw new DataValidationException("Birth date cannot be in the future.");
        
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate());
        actor.setEnabled(true);
        actorRepository.save(actor);

        return new ActorRegistratioDTO(actor.getName(),actor.getBirthDate());
    }

    public ActorDTO remove(Long id) {
        if(id == null)
            throw new DataValidationException("Actor id must be informed.");
        
        Optional<Actor> actor = actorRepository.findById(id);

        if(actor.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Actor removed = actor.get();
        actorRepository.deleteById(id);;

        return new ActorDTO(removed.getId(),removed.getName(),removed.getBirthDate(),removed.getMidias());
    }



}
