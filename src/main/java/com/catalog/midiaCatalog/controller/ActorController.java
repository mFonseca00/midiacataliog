package com.catalog.midiacatalog.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.midiacatalog.dto.Actor.ActorRegistratioDTO;
import com.catalog.midiacatalog.service.ActorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/actor")
public class ActorController {

    @Autowired
    ActorService actorService;

    @PostMapping("/register")
    public ActorRegistratioDTO register(@RequestBody @Valid ActorRegistratioDTO actorDTO){
        return actorService.register(actorDTO);
    }
}
