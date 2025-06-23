package com.catalog.midiacatalog.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistrationDTO;
import com.catalog.midiacatalog.dto.Actor.ActorResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaDTO;
import com.catalog.midiacatalog.service.ActorService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/actor")
public class ActorController {

    @Autowired
    ActorService actorService;

    @PostMapping("/register")
    public ActorResponseDTO register(@RequestBody @Valid ActorRegistrationDTO actorDTO){
        return actorService.register(actorDTO);
    }

    @DeleteMapping("/remove/{id}")
    public ActorResponseDTO remove(@PathVariable Long id){
        return actorService.remove(id);
    }

    @GetMapping("/{id}")
    public ActorDTO getActor(@PathVariable Long id){
        return actorService.getActor(id);
    }
    
    @GetMapping("/list") //TODO: adicionar paginação
    public List<ActorDTO> getAllActors(){
        return actorService.getAllActors();
    }

    @PutMapping("/{id}/add-midia/{midiaId}")
    public String addMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        return actorService.addMidia(id, midiaId);
    }


    @DeleteMapping("/{id}/remove-midia/{midiaId}")
    public MidiaDTO removeMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        return actorService.removeMidia(id, midiaId);
    }

    @GetMapping("{id}/list-midias") //TODO: adicionar paginação
    public List<MidiaDTO> getAllActorMidias(@PathVariable Long id){
        return actorService.getAllActorMidias(id);
    }

    //TODO: updateActor

}
