package com.catalog.midiaCatalog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private Actor actor1;
    private Actor actor2;
    private Actor actor3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new Actor();
        actor2 = new Actor();
        actor3 = new Actor();
        actor1.setName("Jhon");
        actor1.setBirthDate(new GregorianCalendar(2000, GregorianCalendar.JANUARY, 1).getTime());
        actor2.setName("Peter");
        actor1.setBirthDate(new GregorianCalendar(1999, GregorianCalendar.FEBRUARY, 10).getTime());
        actor3.setName("Paula");
        actor1.setBirthDate(new GregorianCalendar(1978, GregorianCalendar.JANUARY, 5).getTime());

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

        when(actorRepository.save(any(Actor.class))).thenReturn(actor1);
    }

    @Test
    void testRegisterActorSuccess() {
        Actor saved = actorService.registerActor(actor1);

        assertNotNull(saved);
        assertEquals(actor1.getName(), saved.getName());
        assertEquals(actor1.getBirthDate(), saved.getBirthDate());
        assertEquals(actor1.getMidias(), saved.getMidias());        
        verify(actorRepository, times(1)).save(actor1);
    }

    @Test
    void testRegisterActorUnNamed() {
        Actor actor = new Actor();

        // Alterar exception
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> {
                actorService.registerActor(actor);
            });

        assertEquals("Actor name must be informed", exception.getMessage());
    }

}
