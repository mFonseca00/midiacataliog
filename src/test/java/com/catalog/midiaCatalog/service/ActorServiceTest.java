package com.catalog.midiaCatalog.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistratioDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.Midia;
import com.catalog.midiacatalog.model.enums.Midiatype;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.repository.MidiaRepository;
import com.catalog.midiacatalog.service.ActorService;

public class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private MidiaRepository midiaRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Midia midia1;
    private Midia midia2;
    private Midia midia3;
    private List<Actor> actors;
    private List<Midia> midias;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new Actor();
        actor1.setName("Jhon");
        actor1.setBirthDate(LocalDate.of(2000, 1, 1));
        actor1.setId(1L);

        actor2 = new Actor();
        actor2.setName("Peter");
        actor2.setBirthDate(LocalDate.of(1999, 2, 10));
        actor2.setId(2L);

        actor3 = new Actor();
        actor3.setName("Paula");
        actor3.setBirthDate(LocalDate.of(1978, 3, 5));
        actor3.setId(3L);
        

        actors = new ArrayList<>();
        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);

        midia1 = new Midia(
            "Title 1",
            Midiatype.MOVIE,
            2024,
            "Director 1",
            "synopses 1",
            "Horror",
            "image1",
            actors
        );
        midia1.setId(1L);

        midia2 = new Midia(
            "Title 2",
            Midiatype.SERIES,
            2025,
            "Director 1",
            "synopses 2",
            "Horror",
            "image2",
            actors
        );
        midia2.setId(2L);

        
        midia3 = new Midia(
            "Title 3",
            Midiatype.SERIES,
            2021,
            "Director 2",
            "synopses 3",
            "Action",
            "image3",
            actors
        );
        midia3.setId(3L);

        midias = new ArrayList<>();
        midias.add(midia1);
        midias.add(midia2);
        midias.add(midia3);
        
    }

    @Test
    void testRegisterActorSuccess() {
        ActorRegistratioDTO actor = new ActorRegistratioDTO();
        actor.setName(actor1.getName());
        actor.setBirthDate(actor1.getBirthDate());
        ActorRegistratioDTO saved = actorService.register(actor);

        assertNotNull(saved);
        assertEquals(actor1.getName(), saved.getName());
        assertEquals(actor1.getBirthDate(), saved.getBirthDate());
        verify(actorRepository, times(1)).save(any(Actor.class));
    }

    @Test
    void testRegisterFailActorUnNamed() {
        ActorRegistratioDTO actor = new ActorRegistratioDTO();
        actor.setBirthDate(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.register(actor);
            });

        assertEquals("Actor name must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterFailFutureDate(){
        ActorRegistratioDTO actor = new ActorRegistratioDTO();
        actor.setName("Joana");
        actor.setBirthDate(LocalDate.of(3000, 1, 1));
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.register(actor);
            });

        assertEquals("Birth date cannot be in the future.", exception.getMessage());
    }

    @Test
    void testRemoveSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        ActorDTO removed = actorService.remove(actor1.getId());

        assertNotNull(removed);
        assertEquals(actor1.getId(), removed.getId());
        assertEquals(actor1.getName(), removed.getName());
        assertEquals(actor1.getBirthDate(), removed.getBirthDate());
        assertEquals(actor1.getMidias(),removed.getMidias());
        verify(actorRepository, times(1)).deleteById(actor1.getId());
    }

    @Test
    void testRemoveFailNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.remove(null);
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
    }

    @Test
    void testRemoveFailNotFound() {
        Long actorId = 1L;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.remove(actorId);
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(actorId);
        verify(actorRepository, never()).deleteById(any());
    }

    @Test
    void testGetActorSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        ActorDTO found = actorService.getActor(actor1.getId());

        assertNotNull(found);
        assertEquals(actor1.getId(), found.getId());
        assertEquals(actor1.getName(), found.getName());
        assertEquals(actor1.getBirthDate(), found.getBirthDate());
        assertEquals(actor1.getMidias(),found.getMidias());
        verify(actorRepository, times(1)).findById(actor1.getId());
    }

    @Test
    void testGetActorFailNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.getActor(null);
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
    }
    
    @Test
    void testGetActorFailActorNotFound(){
        Long actorId = 1L;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.getActor(actorId);
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(actorId);
    }

    @Test
    void testGetAllActorsSuccess(){
        
        when(actorRepository.findAll()).thenReturn(actors);

        List<ActorDTO> found = actorService.getAllActors();
        assertNotNull(found);
        assertEquals(3, found.size());
        assertEquals(actor1.getName(), found.get(0).getName());
        assertEquals(actor2.getName(), found.get(1).getName());
        assertEquals(actor3.getName(), found.get(2).getName());
        verify(actorRepository, times(1)).findAll();

    }

    @Test
    void testGetAllActorsEmptyList() {
        when(actorRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.getAllActors();
            });

        assertEquals("No actors found in database.", exception.getMessage());
        verify(actorRepository, times(1)).findAll();
    }

    @Test
    void testAddMidiaSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));

        String response = actorService.addMidia(actor1.getId(),midia1.getId());
        
        assertNotNull(response);
        assertEquals(response, "Midia added successfully");
        verify(actorRepository, times(1)).save(actor1);
    }

    @Test
    void testAddMidiaFailNullActorId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.addMidia(null, midia1.getId());
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
    }

    @Test
    void testAddMidiaFailActorNotFound(){
        Long actorId = 1L;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.addMidia(actorId, midia1.getId());
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(actorId);
    }

    @Test
    void testAddMidiaFailNullMidiaId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.addMidia(actor1.getId(), null);
            });

        assertEquals("Midia id must be informed.", exception.getMessage());
    }

    @Test
    void testAddMidiaFailMidiaNotFound(){
        Long midiaId = 1L;
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));
        when(midiaRepository.findById(midiaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> actorService.addMidia(actor1.getId(), midiaId));

        assertEquals("Midia not found.", exception.getMessage());
        verify(midiaRepository, times(1)).findById(midiaId);
        verify(actorRepository, never()).save(any());
    }

    @Test
    void removeMidiaSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));

        MidiaDTO removed = actorService.removeMidia(actor1.getId(),midia1.getId());

        assertNotNull(removed);
        assertEquals(midia1.getId(),removed.getId());
        assertEquals(midia1.getTitle(),removed.getTitle());
        assertEquals(midia1.getActors(),removed.getActors());
        assertEquals(midia1.getDirector(),removed.getDirector());
        assertEquals(midia1.getGenre(),removed.getGenre());
        assertEquals(midia1.getPoseterImageUrl(),removed.getPoseterImageUrl());
        assertEquals(midia1.getSynopsis(),removed.getSynopsis());        
        assertEquals(midia1.getType(),removed.getType());        
        verify(midiaRepository, times(1)).findById(midia1.getId());
        verify(actorRepository, times(1)).save(actor1);
    }

    @Test
    void testRemoveMidiaFailActorNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.removeMidia(null, midia1.getId());
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
    }

    @Test
    void testRemoveMidiaFailActorNotFound(){
        Long actorId = 1L;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.removeMidia(actorId, midia1.getId());
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(actorId);
    }

    @Test
    void testRemoveMidiaFailMidiaNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.removeMidia(actor1.getId(), null);
            });

        assertEquals("Midia id must be informed.", exception.getMessage());
    }

    @Test
    void testRemoveMidiaFailMidiaNotFound(){
        Long midiaId = 1L;
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));
        when(midiaRepository.findById(midiaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> actorService.removeMidia(actor1.getId(), midiaId));

        assertEquals("Midia not found.", exception.getMessage());
        verify(midiaRepository, times(1)).findById(midiaId);
        verify(actorRepository, never()).save(any());
    }

    @Test
    void testGetActorMidias(){
        //ToDo
    }
}
