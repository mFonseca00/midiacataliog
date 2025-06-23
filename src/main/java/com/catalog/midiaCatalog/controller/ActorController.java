package com.catalog.midiacatalog.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistrationDTO;
import com.catalog.midiacatalog.dto.Actor.ActorResponseDTO;
import com.catalog.midiacatalog.dto.Actor.ActorUpdateDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaDTO;
import com.catalog.midiacatalog.service.ActorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;


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

    @PatchMapping("/update/{id}")
    public ActorResponseDTO update(@PathVariable Long id, @RequestBody @Valid ActorUpdateDTO actorInfo){
        return actorService.update(id, actorInfo);
    }

    @GetMapping("/{id}")
    public ActorDTO getActor(@PathVariable Long id){
        return actorService.getActor(id);
    }
    
    @GetMapping("/list")
    public Page<ActorDTO> getAllActors(
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return actorService.getAllActors(pageable);
    }

    @PutMapping("/{id}/add-midia/{midiaId}")
    public String addMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        return actorService.addMidia(id, midiaId);
    }


    @DeleteMapping("/{id}/remove-midia/{midiaId}")
    public MidiaDTO removeMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        return actorService.removeMidia(id, midiaId);
    }

    @GetMapping("{id}/list-midias") 
    public Page<MidiaDTO> getAllActorMidias(
        @PathVariable Long id,
        @PageableDefault(size = 10, sort = "name") Pageable pageable){
        return actorService.getAllActorMidias(id, pageable);
    }

}
