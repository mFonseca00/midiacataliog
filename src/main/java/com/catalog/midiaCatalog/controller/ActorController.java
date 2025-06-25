package com.catalog.midiacatalog.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<ActorResponseDTO> register(@RequestBody @Valid ActorRegistrationDTO actorDTO){
        ActorResponseDTO response = actorService.register(actorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ActorResponseDTO> remove(@PathVariable Long id){
        ActorResponseDTO response = actorService.remove(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ActorResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ActorUpdateDTO actorInfo){
        ActorResponseDTO response = actorService.update(id, actorInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActor(@PathVariable Long id){
        ActorDTO response = actorService.getActor(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/list")
    public ResponseEntity<Page<ActorDTO>> getAllActors(
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<ActorDTO> response = actorService.getAllActors(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/add-midia/{midiaId}")
    public ResponseEntity<String> addMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        String response = actorService.addMidia(id, midiaId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/remove-midia/{midiaId}")
    public ResponseEntity<MidiaDTO> removeMidia(@PathVariable Long id, @PathVariable Long midiaId) {
        MidiaDTO response = actorService.removeMidia(id, midiaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/list-midias") 
    public ResponseEntity<Page<MidiaDTO>> getAllActorMidias(
        @PathVariable Long id,
        @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<MidiaDTO> response = actorService.getAllActorMidias(id, pageable);
        return ResponseEntity.ok(response);
    }
}
