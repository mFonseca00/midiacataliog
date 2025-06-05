package com.catalog.midiacatalog.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.ActorDTO;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.repository.ActorRepository;

@Service
public class ActorService {

    private ActorRepository actorRepository;

    public ActorDTO registerActor(ActorDTO actorDTO) {
        if(actorDTO.getName() == null || actorDTO.getName() == "")
            throw new DataValidationException("Actor name must be informed");
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        date.setLenient(false);
        
        if(actorDTO.getBirthDate().after(new Date())) 
            throw new DataValidationException("Birth date cannot be in the future");
        
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate());
        actorRepository.save(actor);

        return new ActorDTO(actor.getName(),actor.getBirthDate());
    }



}
