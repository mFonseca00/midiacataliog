package com.catalog.midiacatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.Midia.DetailedMidiaResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaRegistrationDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaUpdateDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.Midia;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.repository.MidiaRepository;

@Service
public class MidiaService {

    @Autowired
    private MidiaRepository midiaRepository;

    @Autowired
    private ActorRepository actorRepository;

    public MidiaResponseDTO register(MidiaRegistrationDTO newMidia) {
        if(newMidia == null)
            throw new DataValidationException("Midia data must be informed.");
        
        List<String> errors = new ArrayList<>();
        
        if(newMidia.getTitle() == null || newMidia.getTitle().trim().isEmpty())
            errors.add("Midia title must be informed.");

        if(newMidia.getType() == null || newMidia.getType().toString().trim().isEmpty())
            errors.add("Midia type must be informed.");

        if(!errors.isEmpty())
            throw new DataValidationException(errors);
        
        List<Actor> actors = new ArrayList<>();

        if (newMidia.getActorIds() != null && !newMidia.getActorIds().isEmpty()) {
            List<Long> notFoundActorIds = new ArrayList<>();
            
            for (Long actorId : newMidia.getActorIds()) {
                Optional<Actor> actorOpt = actorRepository.findById(actorId);
                if (actorOpt.isPresent()) {
                    actors.add(actorOpt.get());
                } else {
                    notFoundActorIds.add(actorId);
                }
            }
            
            if (!notFoundActorIds.isEmpty()) {
                throw new DataNotFoundException("The following actor IDs were not found: " + notFoundActorIds);
            }
            
        }

        Midia midia = new Midia(newMidia.getTitle(), newMidia.getType(), newMidia.getReleaseYear(),
                                newMidia.getDirector(), newMidia.getSynopsis(), newMidia.getGenre(),
                                newMidia.getPoseterImageUrl(), actors);
        
        midia = midiaRepository.save(midia);

        return new MidiaResponseDTO(midia.getId(), midia.getTitle(), midia.getType());
    }

    public MidiaResponseDTO remove(Long id) {
        if(id == null)
            throw new DataValidationException("Midia ID must be informed.");

        Optional<Midia> midiaFound = midiaRepository.findById(id);
        if(!midiaFound.isPresent())
            throw new DataNotFoundException("No midia found for this ID.");

        Midia midia = midiaFound.get();
        midiaRepository.deleteById(id);
        return new MidiaResponseDTO(midia.getId(), midia.getTitle(), midia.getType());
    }

    public MidiaResponseDTO update(Long id, MidiaUpdateDTO midiaInfo){
        if(id == null)
            throw new DataValidationException("Midia ID must be informed.");
        if(midiaInfo == null)
            throw new DataValidationException("Midia data must be informed.");
        
        Optional<Midia> midiaFound = midiaRepository.findById(id);
        if(!midiaFound.isPresent())
            throw new DataNotFoundException("No midia found for this ID.");
            
        Midia midia = midiaFound.get();
        
        if(midiaInfo.getTitle() != null && !midiaInfo.getTitle().trim().isEmpty())
            midia.setTitle(midiaInfo.getTitle());
            
        if(midiaInfo.getType() != null)
            midia.setType(midiaInfo.getType());
            
        if(midiaInfo.getReleaseYear() != null)
            midia.setReleaseYear(midiaInfo.getReleaseYear());
            
        if(midiaInfo.getDirector() != null)
            midia.setDirector(midiaInfo.getDirector());
            
        if(midiaInfo.getSynopsis() != null)
            midia.setSynopsis(midiaInfo.getSynopsis());
            
        if(midiaInfo.getGenre() != null)
            midia.setGenre(midiaInfo.getGenre());
            
        if(midiaInfo.getPoseterImageUrl() != null)
            midia.setPoseterImageUrl(midiaInfo.getPoseterImageUrl());
        
        if (midiaInfo.getActorIds() != null) {
            if (midiaInfo.getActorIds().isEmpty()) {
                midia.setActors(new ArrayList<>());
            } else {
                List<Actor> actors = new ArrayList<>();
                List<Long> notFoundActorIds = new ArrayList<>();
                
                for (Long actorId : midiaInfo.getActorIds()) {
                    Optional<Actor> actorOpt = actorRepository.findById(actorId);
                    if (actorOpt.isPresent()) {
                        actors.add(actorOpt.get());
                    } else {
                        notFoundActorIds.add(actorId);
                    }
                }
                
                if (!notFoundActorIds.isEmpty()) {
                    throw new DataNotFoundException("The following actor IDs were not found: " + notFoundActorIds);
                }
                
                midia.setActors(actors);
            }
        }
        
        midiaRepository.save(midia);
        
        return new MidiaResponseDTO(midia.getId(), midia.getTitle(), midia.getType());
    }

    public DetailedMidiaResponseDTO getMidia(Long id) {
        if(id == null)
            throw new DataValidationException("Midia id must be informed.");
        
        Optional<Midia> midiaFound = midiaRepository.findById(id);
        if(!midiaFound.isPresent())
            throw new DataNotFoundException("Midia not found.");

        Midia midia = midiaFound.get();
        return new DetailedMidiaResponseDTO(
            midia.getId(), midia.getTitle(), midia.getType(), midia.getReleaseYear(),
            midia.getDirector(), midia.getSynopsis(), midia.getGenre(),
            midia.getPoseterImageUrl(), midia.getActors() );
    }

    public Page<DetailedMidiaResponseDTO> getAllMidias(Pageable pageable) {
        Page<Midia> midias = midiaRepository.findAll(pageable);

        if(midias.isEmpty())
            throw new DataNotFoundException("No users found in database.");
        
        return midias.map(midia -> new DetailedMidiaResponseDTO(
            midia.getId(), midia.getTitle(), midia.getType(), midia.getReleaseYear(),
            midia.getDirector(), midia.getSynopsis(), midia.getGenre(),
            midia.getPoseterImageUrl(), midia.getActors()
            ));
    }
}
