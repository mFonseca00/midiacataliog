package com.catalog.midiacatalog.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistrationDTO;
import com.catalog.midiacatalog.dto.Actor.ActorResponseDTO;
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

    public ActorResponseDTO register(ActorRegistrationDTO actorDTO) {
        if(actorDTO.getName() == null || actorDTO.getName() == "")
            throw new DataValidationException("Actor name must be informed.");
        
        if(actorDTO.getBirthDate().isAfter(java.time.LocalDate.now())) 
            throw new DataValidationException("Birth date cannot be in the future.");
        
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate());
        actor.setEnabled(true);
        actorRepository.save(actor);

        return new ActorResponseDTO(actor.getId(),actor.getName(),actor.getBirthDate());
    }

    public ActorResponseDTO remove(Long id) {
        if(id == null)
            throw new DataValidationException("Actor id must be informed.");
        
        Optional<Actor> actor = actorRepository.findById(id);

        if(actor.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Actor removed = actor.get();
        actorRepository.deleteById(id);;

        return new ActorResponseDTO(removed.getId(),removed.getName(),removed.getBirthDate());
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
        if (actorId == null)
            throw new DataValidationException("Actor id must be informed.");

        if (midiaId == null)
            throw new DataValidationException("Midia id must be informed.");

        Optional<Actor> actorFound = actorRepository.findById(actorId);

        if (actorFound.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Actor actor = actorFound.get();
        List<Midia> actorMidias = actor.getMidias();

        Midia midiaToRemove = actorMidias.stream()
            .filter(midia -> midia.getId().equals(midiaId))
            .findFirst()
            .orElseThrow(() -> new DataNotFoundException("No midias found for this actor."));

        actorMidias.remove(midiaToRemove);
        actorRepository.save(actor);

        return new MidiaDTO(
            midiaToRemove.getId(),
            midiaToRemove.getTitle(),
            midiaToRemove.getType(),
            midiaToRemove.getReleaseYear(),
            midiaToRemove.getDirector(),
            midiaToRemove.getSynopsis(),
            midiaToRemove.getGenre(),
            midiaToRemove.getPoseterImageUrl(),
            midiaToRemove.getActors()
        );
    }

    public List<MidiaDTO> getAllActorMidias(Long actorId) {
        if(actorId == null)
            throw new DataValidationException("Actor id must be informed.");

        Optional<Actor> actorFound = actorRepository.findById(actorId);

        if(actorFound.isEmpty())
            throw new DataNotFoundException("Actor not found.");

        Actor actor = actorFound.get();
        List<Midia> midias = actor.getMidias();
        if(midias.isEmpty()){
            throw new DataNotFoundException("No midias found for this actor.");
        }

        return midias.stream()
        .map(midia -> new MidiaDTO(
            midia.getId(),
            midia.getTitle(),
            midia.getType(),
            midia.getReleaseYear(),
            midia.getDirector(),
            midia.getSynopsis(),
            midia.getGenre(),
            midia.getPoseterImageUrl(),
            midia.getActors()
        ))
        .collect(Collectors.toList());
    }
}
