package com.catalog.midiaCatalog.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.catalog.midiacatalog.dto.Actor.ActorRegistrationDTO;
import com.catalog.midiacatalog.dto.Actor.ActorResponseDTO;
import com.catalog.midiacatalog.dto.Actor.ActorUpdateDTO;
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
    private Midia midia1;
    private Midia midia2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new Actor();
        actor1.setId(1L);
        actor1.setName("John");
        actor1.setBirthDate(LocalDate.of(2000, 1, 1));
        actor1.setEnabled(true);

        actor2 = new Actor();
        actor2.setId(2L);
        actor2.setName("Mary");
        actor2.setBirthDate(LocalDate.of(1995, 6, 15));
        actor2.setEnabled(true);

        midia1 = new Midia();
        midia1.setId(1L);
        midia1.setTitle("Movie 1");
        midia1.setType(Midiatype.MOVIE);

        midia2 = new Midia();
        midia2.setId(2L);
        midia2.setTitle("Series 1");
        midia2.setType(Midiatype.SERIES);
    }

    @Test
    void testRegisterActorSuccess() {
        ActorRegistrationDTO actor = new ActorRegistrationDTO();
        actor.setName(actor1.getName());
        actor.setBirthDate(actor1.getBirthDate());
        ActorResponseDTO saved = actorService.register(actor);

        assertNotNull(saved);
        assertEquals(actor1.getName(), saved.getName());
        assertEquals(actor1.getBirthDate(), saved.getBirthDate());
        verify(actorRepository, times(1)).save(any(Actor.class));
    }

    @Test
    void testRegisterFailActorUnNamed() {
        ActorRegistrationDTO actor = new ActorRegistrationDTO();
        actor.setBirthDate(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.register(actor);
            });

        assertEquals("Actor name must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterFailFutureDate(){
        ActorRegistrationDTO actor = new ActorRegistrationDTO();
        actor.setName("Joana");
        actor.setBirthDate(LocalDate.of(3000, 1, 1));
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.register(actor);
            });

        assertEquals("Birth date cannot be in the future.", exception.getMessage());
    }

    @Test
    void testUpdateSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        ActorUpdateDTO actorInfo = new ActorUpdateDTO(actor1.getName(), actor2.getBirthDate());

        ActorResponseDTO updated = actorService.update(actor1.getId(), actorInfo);

        assertNotNull(updated);
        assertEquals(actor1.getId(), updated.getId());
        assertEquals(actor1.getName(), updated.getName());
        assertEquals(actor2.getBirthDate(), updated.getBirthDate());
        verify(actorRepository, times(1)).save(any(Actor.class));
    }

    @Test
    void testUpdateValidations(){
        // Test null id

        // Test empty id

        // Test id not found

        // Test all info fields empty

        // Test birthdate future date

    }

    @Test
    void testRemoveSuccess(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        ActorResponseDTO removed = actorService.remove(actor1.getId());

        assertNotNull(removed);
        assertEquals(actor1.getId(), removed.getId());
        assertEquals(actor1.getName(), removed.getName());
        assertEquals(actor1.getBirthDate(), removed.getBirthDate());
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
        
        when(actorRepository.findAll()).thenReturn(Arrays.asList(actor1, actor2));

        List<ActorDTO> found = actorService.getAllActors();
        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals(actor1.getName(), found.get(0).getName());
        assertEquals(actor2.getName(), found.get(1).getName());
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
    void removeMidiaSuccess() {
        List<Midia> midiaList = new ArrayList<>();
        midiaList.add(midia1);
        midiaList.add(midia2);
        
        actor1.setMidias(midiaList);
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        MidiaDTO removed = actorService.removeMidia(actor1.getId(), midia1.getId());

        assertNotNull(removed);
        assertEquals(midia1.getId(), removed.getId());
        assertEquals(midia1.getTitle(), removed.getTitle());
        assertEquals(midia1.getActors(), removed.getActors());
        assertEquals(midia1.getDirector(), removed.getDirector());
        assertEquals(midia1.getGenre(), removed.getGenre());
        assertEquals(midia1.getPoseterImageUrl(), removed.getPoseterImageUrl());
        assertEquals(midia1.getSynopsis(), removed.getSynopsis());        
        assertEquals(midia1.getType(), removed.getType());        
        verify(actorRepository, times(1)).save(actor1);
    }

    @Test
    void testRemoveMidiaFailActorNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.removeMidia(null, midia1.getId());
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
        verify(actorRepository, never()).save(any());
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
        verify(actorRepository, never()).save(any());
    }

    @Test
    void testRemoveMidiaFailMidiaNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.removeMidia(actor1.getId(), null);
            });

        assertEquals("Midia id must be informed.", exception.getMessage());
        verify(actorRepository, never()).save(any());
    }

    @Test
    void testRemoveMidiaFailMidiaNotFound(){
        Long midiaId = 1L;
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> actorService.removeMidia(actor1.getId(), midiaId));

        assertEquals("No midias found for this actor.", exception.getMessage());
        verify(actorRepository, times(1)).findById(midiaId);
        verify(actorRepository, never()).save(any());
    }

    @Test
    void testGetAllActorMidiasSuccess() {
        List<Midia> midiaList = new ArrayList<>();
        midiaList.add(midia1);
        midiaList.add(midia2);
        
        actor1.setMidias(midiaList);
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        List<MidiaDTO> found = actorService.getAllActorMidias(actor1.getId());

        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals(midia1.getTitle(), found.get(0).getTitle());
        assertEquals(midia2.getTitle(), found.get(1).getTitle());
        verify(actorRepository, times(1)).findById(actor1.getId());
    }

    @Test
    void testGetAllActorMidiasFailActorNotFound(){
        Long actorId = 1L;
        when(actorRepository.findById(actorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.getAllActorMidias(actorId);
            });

        assertEquals("Actor not found.", exception.getMessage());
        verify(actorRepository, times(1)).findById(actorId);
    }

    @Test
    void testGetAllActorMidiasFailActorNullId(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                actorService.getAllActorMidias(null);
            });

        assertEquals("Actor id must be informed.", exception.getMessage());
    }

    @Test
    void testGetAllActorMidiasFailEmptyList(){
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                actorService.getAllActorMidias(actor1.getId());
            });

        assertEquals("No midias found for this actor.", exception.getMessage());
    }

    @Test
    void testRegisterActorValidations() {
        // Test null actor
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> actorService.register(null));
        assertEquals("Actor data must be informed.", exception.getMessage());

        // Test empty fields
        ActorRegistrationDTO emptyActor = new ActorRegistrationDTO();
        exception = assertThrows(DataValidationException.class,
            () -> actorService.register(emptyActor));
        assertTrue(exception.getErrors().contains("Actor name must be informed."));
        assertTrue(exception.getErrors().contains("Birth date must be informed."));

        // Test future date
        ActorRegistrationDTO futureActor = new ActorRegistrationDTO();
        futureActor.setName("Test");
        futureActor.setBirthDate(LocalDate.now().plusYears(1));
        exception = assertThrows(DataValidationException.class,
            () -> actorService.register(futureActor));
        assertTrue(exception.getErrors().contains("Birth date cannot be in the future."));
    }

    @Test
    void testActorOperationsValidations() {
        // Test get actor validations
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> actorService.getActor(null));
        assertEquals("Actor id must be informed.", exception.getMessage());

        // Test remove actor validations
        exception = assertThrows(DataValidationException.class,
            () -> actorService.remove(null));
        assertEquals("Actor id must be informed.", exception.getMessage());

        // Test get all actor midias validations
        exception = assertThrows(DataValidationException.class,
            () -> actorService.getAllActorMidias(null));
        assertEquals("Actor id must be informed.", exception.getMessage());
    }

    @Test
    void testMidiaOperationsValidations() {
        // Add midia validations
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> actorService.addMidia(null, 1L));
        assertEquals("Actor id must be informed.", exception.getMessage());

        exception = assertThrows(DataValidationException.class,
            () -> actorService.addMidia(1L, null));
        assertEquals("Midia id must be informed.", exception.getMessage());

        // Remove midia validations
        exception = assertThrows(DataValidationException.class,
            () -> actorService.removeMidia(null, 1L));
        assertEquals("Actor id must be informed.", exception.getMessage());

        exception = assertThrows(DataValidationException.class,
            () -> actorService.removeMidia(1L, null));
        assertEquals("Midia id must be informed.", exception.getMessage());
    }

    @Test
    void testSuccessfulOperations() {
        // Test register
        ActorRegistrationDTO validActor = new ActorRegistrationDTO();
        validActor.setName(actor1.getName());
        validActor.setBirthDate(actor1.getBirthDate());
        
        when(actorRepository.save(any())).thenReturn(actor1);
        ActorResponseDTO response = actorService.register(validActor);
        assertEquals(actor1.getName(), response.getName());

        // Test get actor
        when(actorRepository.findById(actor1.getId())).thenReturn(Optional.of(actor1));
        ActorDTO found = actorService.getActor(actor1.getId());
        assertEquals(actor1.getName(), found.getName());

        // Test get all actors
        when(actorRepository.findAll()).thenReturn(Arrays.asList(actor1, actor2));
        List<ActorDTO> allActors = actorService.getAllActors();
        assertEquals(2, allActors.size());

        // Test add midia
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        actor1.setMidias(new ArrayList<>());
        String addResponse = actorService.addMidia(actor1.getId(), midia1.getId());
        assertEquals("Midia added successfully", addResponse);

        // Test remove midia
        actor1.getMidias().add(midia1);
        MidiaDTO removedMidia = actorService.removeMidia(actor1.getId(), midia1.getId());
        assertEquals(midia1.getId(), removedMidia.getId());
    }

    @Test
    void testNotFoundScenarios() {
        when(actorRepository.findById(99L)).thenReturn(Optional.empty());
        when(midiaRepository.findById(99L)).thenReturn(Optional.empty());

        // Test actor not found
        assertThrows(DataNotFoundException.class,
            () -> actorService.getActor(99L));

        // Test midia not found
        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        assertThrows(DataNotFoundException.class,
            () -> actorService.addMidia(1L, 99L));

        // Test empty lists
        when(actorRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(DataNotFoundException.class,
            () -> actorService.getAllActors());

        actor1.setMidias(new ArrayList<>());
        assertThrows(DataNotFoundException.class,
            () -> actorService.getAllActorMidias(1L));
    }

}
