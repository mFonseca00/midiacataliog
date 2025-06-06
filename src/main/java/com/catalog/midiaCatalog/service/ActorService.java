package com.catalog.midiacatalog.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistratioDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.Midia;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.repository.MidiaRepository;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MidiaRepository midiaRepository;

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

        return new ActorDTO(
            removed.getId(),
            removed.getName(),
            removed.getBirthDate(),
            removed.getMidias());
    }

    public ActorDTO getActor(Long id) {
        if(id == null)
            throw new DataValidationException("Actor id must be informed.");

        Optional<Actor> actor = actorRepository.findById(id);

        if(actor.isEmpty())
            throw new DataNotFoundException("Actor not found.");
        
        Actor found = actor.get();

        return new ActorDTO(
            found.getId(),
            found.getName(),
            found.getBirthDate(),
            found.getMidias());
    }

    public List<ActorDTO> getAllActors() {
        List<Actor> actors = actorRepository.findAll();
        
        if(actors == null || actors.isEmpty())
            throw new DataNotFoundException("No actors found in database.");

        return actors.stream()
            .map(actor -> new ActorDTO(
                actor.getId(), 
                actor.getName(), 
                actor.getBirthDate(), 
                actor.getMidias()))
            .collect(Collectors.toList());
    }

    public String addMidia(Long actorId, Long midiaId) {
        if(actorId == null)
            throw new DataValidationException("Actor id must be informed.");
        
        if(midiaId == null)
            throw new DataValidationException("Midia id must be informed.");

        Optional<Actor> actorFound = actorRepository.findById(actorId);

        if(actorFound.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Optional<Midia> midiaFound = midiaRepository.findById(midiaId);
        
        if(midiaFound.isEmpty())
            throw new DataNotFoundException("Midia not found.");

        Actor actor = actorFound.get();
        Midia midia = midiaFound.get();
        List<Midia> actorMidias = actor.getMidias();
        actorMidias.add(midia);
        actorRepository.save(actor);

        return "Midia added successfully";
    }

    public MidiaDTO removeMidia(Long actorId, Long midiaId) {
        if(actorId == null)
            throw new DataValidationException("Actor id must be informed.");
        
        if(midiaId == null)
            throw new DataValidationException("Midia id must be informed.");

        Optional<Actor> actorFound = actorRepository.findById(actorId);

        if(actorFound.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Optional<Midia> midiaFound = midiaRepository.findById(midiaId);
        
        if(midiaFound.isEmpty())
            throw new DataNotFoundException("Midia not found.");

        Actor actor = actorFound.get();
        Midia midia = midiaFound.get();
        List<Midia> actorMidias = actor.getMidias();
        actorMidias.remove(midia);
        actorRepository.save(actor);

        return new MidiaDTO(
            midia.getId(),
            midia.getTitle(),
            midia.getType(),
            midia.getReleaseYear(),
            midia.getDirector(),
            midia.getSynopsis(),
            midia.getGenre(),
            midia.getPoseterImageUrl(),
            midia.getActors());
    }



}
