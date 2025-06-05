package com.catalog.midiaCatalog.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.catalog.midiacatalog.dto.ActorDTO;
import com.catalog.midiacatalog.dto.ActorRegistratioDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.service.ActorService;

public class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor actor1;
    private Actor actor2;
    private Actor actor3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new Actor();
        actor2 = new Actor();
        actor3 = new Actor();
        actor1.setId(1L);
        actor1.setName("Jhon");
        actor1.setBirthDate(LocalDate.of(2000, 1, 1));
        actor1.setId(2L);
        actor2.setName("Peter");
        actor2.setBirthDate(LocalDate.of(1999, 2, 10));
        actor1.setId(3L);
        actor3.setName("Paula");
        actor3.setBirthDate(LocalDate.of(1978, 3, 5));

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
        when(actorRepository.findById(5L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.remove(5L);
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(5L);
        verify(actorRepository, never()).deleteById(any());
    }
    
}
