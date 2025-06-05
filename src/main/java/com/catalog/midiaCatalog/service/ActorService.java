package com.catalog.midiacatalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.ActorRegistratioDTO;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.repository.ActorRepository;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    public ActorRegistratioDTO register(ActorRegistratioDTO actorDTO) {
        if(actorDTO.getName() == null || actorDTO.getName() == "")
            throw new DataValidationException("Actor name must be informed");
        
        if(actorDTO.getBirthDate().isAfter(java.time.LocalDate.now())) 
            throw new DataValidationException("Birth date cannot be in the future");
        
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate());
        actor.setEnabled(true);
        actorRepository.save(actor);

        return new ActorRegistratioDTO(actor.getName(),actor.getBirthDate());
    }



}
