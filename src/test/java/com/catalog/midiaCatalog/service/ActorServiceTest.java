package com.catalog.midiaCatalog.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.Midia;
import com.catalog.midiacatalog.model.enums.Midiatype;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.service.ActorService;

public class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    private ActorDTO actor1;
    private ActorDTO actor2;
    private ActorDTO actor3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new ActorDTO();
        actor2 = new ActorDTO();
        actor3 = new ActorDTO();
        actor1.setName("Jhon");
        actor1.setBirthDate(LocalDate.of(2000, 1, 1));
        actor2.setName("Peter");
        actor2.setBirthDate(LocalDate.of(1999, 2, 10));
        actor3.setName("Paula");
        actor3.setBirthDate(LocalDate.of(1978, 3, 5));

        List<Midia> midias = new ArrayList<>();
        Midia midia = new Midia();

        midia.setTitle("Exemple");
        midia.setType(Midiatype.MOVIE);
        midias.add(midia);

        actor1.setMidias(midias);

        midia.setTitle("Exemple 2");
        midia.setType(Midiatype.SERIES);
        midias.add(midia);

        actor1.setMidias(midias);
        actor2.setMidias(midias);

        midia.setTitle("Exemple 3");
        midia.setType(Midiatype.MOVIE);
        midias.add(midia);

        actor3.setMidias(midias);

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

        assertEquals("Actor name must be informed", exception.getMessage());
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

        assertEquals("Birth date cannot be in the future", exception.getMessage());
    }

    @Test
    void testRemoveSuccess(){
        
    }
    
}
