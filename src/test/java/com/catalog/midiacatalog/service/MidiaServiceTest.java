package com.catalog.midiacatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.catalog.midiacatalog.dto.Midia.DetailedMidiaResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaRegistrationDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaUpdateDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
import com.catalog.midiacatalog.model.Midia;
import com.catalog.midiacatalog.model.enums.Midiatype;
import com.catalog.midiacatalog.repository.ActorRepository;
import com.catalog.midiacatalog.repository.MidiaRepository;

public class MidiaServiceTest {
    @Mock
    private MidiaRepository midiaRepository;

    @Mock
    private ActorRepository actorRepository;

    @InjectMocks
    private MidiaService midiaService;

    private Midia midia1;
    private Midia midia2;
    private Actor actor1;
    private Actor actor2;
    private List<Actor> actors;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        actor1 = new Actor();
        actor1.setId(1L);
        actor1.setName("John Doe");

        actor2 = new Actor();
        actor2.setId(2L);
        actor2.setName("Jane Smith");

        actors = Arrays.asList(actor1, actor2);

        midia1 = new Midia();
        midia1.setId(1L);
        midia1.setTitle("The Matrix");
        midia1.setType(Midiatype.MOVIE);
        midia1.setReleaseYear(1999);
        midia1.setDirector("Wachowski Brothers");
        midia1.setSynopsis("A computer hacker learns about the true nature of reality");
        midia1.setGenre("Sci-Fi");
        midia1.setPoseterImageUrl("matrix.jpg");
        midia1.setActors(new ArrayList<>(actors));
        midia1.setEnabled(true);

        midia2 = new Midia();
        midia2.setId(2L);
        midia2.setTitle("Breaking Bad");
        midia2.setType(Midiatype.SERIES);
        midia2.setReleaseYear(2008);
        midia2.setDirector("Vince Gilligan");
        midia2.setSynopsis("A high school chemistry teacher turned methamphetamine producer");
        midia2.setGenre("Drama");
        midia2.setPoseterImageUrl("breakingbad.jpg");
        midia2.setActors(new ArrayList<>());
        midia2.setEnabled(true);
    }

    @Test
    void testRegisterMidiaSuccess() {
        MidiaRegistrationDTO midiaDTO = new MidiaRegistrationDTO();
        midiaDTO.setTitle(midia1.getTitle());
        midiaDTO.setType(midia1.getType());
        midiaDTO.setReleaseYear(midia1.getReleaseYear());
        midiaDTO.setDirector(midia1.getDirector());
        midiaDTO.setSynopsis(midia1.getSynopsis());
        midiaDTO.setGenre(midia1.getGenre());
        midiaDTO.setPoseterImageUrl(midia1.getPoseterImageUrl());
        midiaDTO.setActorIds(Arrays.asList(1L, 2L));

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        when(actorRepository.findById(2L)).thenReturn(Optional.of(actor2));
        when(midiaRepository.save(any(Midia.class))).thenReturn(midia1);

        MidiaResponseDTO response = midiaService.register(midiaDTO);

        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        assertEquals(midia1.getTitle(), response.getTitle());
        assertEquals(midia1.getType(), response.getType());
        verify(midiaRepository, times(1)).save(any(Midia.class));
    }

    @Test
    void testRegisterMidiaFailNullMidia() {
        Exception exception = assertThrows(DataValidationException.class,
            () -> midiaService.register(null));
        assertEquals("Midia data must be informed.", exception.getMessage());
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testRegisterMidiaFailMissingRequiredFields() {
        MidiaRegistrationDTO midiaDTO = new MidiaRegistrationDTO();
        
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> midiaService.register(midiaDTO));
        
        List<String> errors = exception.getErrors();
        assertTrue(errors.contains("Midia title must be informed."));
        assertTrue(errors.contains("Midia type must be informed."));
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testRegisterMidiaFailActorNotFound() {
        MidiaRegistrationDTO midiaDTO = new MidiaRegistrationDTO();
        midiaDTO.setTitle(midia1.getTitle());
        midiaDTO.setType(midia1.getType());
        midiaDTO.setActorIds(Arrays.asList(1L, 99L));

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        when(actorRepository.findById(99L)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.register(midiaDTO));
        
        assertTrue(exception.getMessage().contains("The following actor IDs were not found:"));
        assertTrue(exception.getMessage().contains("99"));
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testRemoveMidiaSuccess() {
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        
        MidiaResponseDTO response = midiaService.remove(midia1.getId());
        
        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        assertEquals(midia1.getTitle(), response.getTitle());
        assertEquals(midia1.getType(), response.getType());
        verify(midiaRepository, times(1)).deleteById(midia1.getId());
    }

    @Test
    void testRemoveMidiaFailNullId() {
        Exception exception = assertThrows(DataValidationException.class,
            () -> midiaService.remove(null));
        assertEquals("Midia ID must be informed.", exception.getMessage());
        verify(midiaRepository, never()).deleteById(any());
    }

    @Test
    void testRemoveMidiaFailNotFound() {
        when(midiaRepository.findById(99L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.remove(99L));
        assertEquals("No midia found for this ID.", exception.getMessage());
        verify(midiaRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateMidiaSuccess() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        midiaDTO.setType(Midiatype.SERIES);
        midiaDTO.setReleaseYear(2000);
        midiaDTO.setDirector("Updated Director");
        midiaDTO.setSynopsis("Updated Synopsis");
        midiaDTO.setGenre("Updated Genre");
        midiaDTO.setPoseterImageUrl("updated.jpg");
        midiaDTO.setActorIds(Arrays.asList(1L));

        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        when(midiaRepository.save(any(Midia.class))).thenReturn(midia1);

        MidiaResponseDTO response = midiaService.update(midia1.getId(), midiaDTO);

        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        verify(midiaRepository, times(1)).save(any(Midia.class));
        
        assertEquals("Updated Title", midia1.getTitle());
        assertEquals(Midiatype.SERIES, midia1.getType());
        assertEquals(2000, midia1.getReleaseYear());
        assertEquals("Updated Director", midia1.getDirector());
        assertEquals("Updated Synopsis", midia1.getSynopsis());
        assertEquals("Updated Genre", midia1.getGenre());
        assertEquals("updated.jpg", midia1.getPoseterImageUrl());
        assertEquals(1, midia1.getActors().size());
        assertEquals(actor1.getId(), midia1.getActors().get(0).getId());
    }

    @Test
    void testUpdateMidiaFailNullId() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        
        Exception exception = assertThrows(DataValidationException.class,
            () -> midiaService.update(null, midiaDTO));
        assertEquals("Midia ID must be informed.", exception.getMessage());
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testUpdateMidiaFailNullData() {
        Exception exception = assertThrows(DataValidationException.class,
            () -> midiaService.update(midia1.getId(), null));
        assertEquals("Midia data must be informed.", exception.getMessage());
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testUpdateMidiaFailNotFound() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        when(midiaRepository.findById(99L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.update(99L, midiaDTO));
        assertEquals("No midia found for this ID.", exception.getMessage());
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testUpdateMidiaFailActorNotFound() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        midiaDTO.setActorIds(Arrays.asList(99L));
        
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        when(actorRepository.findById(99L)).thenReturn(Optional.empty());
        
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.update(midia1.getId(), midiaDTO));
        
        assertTrue(exception.getMessage().contains("The following actor IDs were not found:"));
        assertTrue(exception.getMessage().contains("99"));
        verify(midiaRepository, never()).save(any(Midia.class));
    }

    @Test
    void testGetMidiaSuccess() {
        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        
        DetailedMidiaResponseDTO response = midiaService.getMidia(midia1.getId());
        
        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        assertEquals(midia1.getTitle(), response.getTitle());
        assertEquals(midia1.getType(), response.getType());
        assertEquals(midia1.getReleaseYear(), response.getReleaseYear());
        assertEquals(midia1.getDirector(), response.getDirector());
        assertEquals(midia1.getSynopsis(), response.getSynopsis());
        assertEquals(midia1.getGenre(), response.getGenre());
        assertEquals(midia1.getPoseterImageUrl(), response.getPoseterImageUrl());
        assertEquals(2, response.getActors().size());
    }

    @Test
    void testGetMidiaFailNullId() {
        Exception exception = assertThrows(DataValidationException.class,
            () -> midiaService.getMidia(null));
        assertEquals("Midia id must be informed.", exception.getMessage());
    }

    @Test
    void testGetMidiaFailNotFound() {
        when(midiaRepository.findById(99L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.getMidia(99L));
        assertEquals("Midia not found.", exception.getMessage());
    }

    @Test
    void testGetAllMidiasSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Midia> midias = Arrays.asList(midia1, midia2);
        Page<Midia> midiaPage = new PageImpl<>(midias, pageable, midias.size());
        
        when(midiaRepository.findAll(pageable)).thenReturn(midiaPage);
        
        Page<DetailedMidiaResponseDTO> response = midiaService.getAllMidias(pageable);
        
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(midia1.getId(), response.getContent().get(0).getId());
        assertEquals(midia1.getTitle(), response.getContent().get(0).getTitle());
        assertEquals(midia2.getId(), response.getContent().get(1).getId());
        assertEquals(midia2.getTitle(), response.getContent().get(1).getTitle());
    }

    @Test
    void testGetAllMidiasFailEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Midia> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        
        when(midiaRepository.findAll(pageable)).thenReturn(emptyPage);
        
        Exception exception = assertThrows(DataNotFoundException.class,
            () -> midiaService.getAllMidias(pageable));
        assertEquals("No users found in database.", exception.getMessage());
    }

    @Test
    void testRegisterMidiaWithActors() {
        MidiaRegistrationDTO midiaDTO = new MidiaRegistrationDTO();
        midiaDTO.setTitle(midia1.getTitle());
        midiaDTO.setType(midia1.getType());
        midiaDTO.setActorIds(Arrays.asList(1L, 2L));

        when(actorRepository.findById(1L)).thenReturn(Optional.of(actor1));
        when(actorRepository.findById(2L)).thenReturn(Optional.of(actor2));
        when(midiaRepository.save(any(Midia.class))).thenAnswer(invocation -> {
            Midia savedMidia = invocation.getArgument(0);
            savedMidia.setId(1L);
            return savedMidia;
        });

        MidiaResponseDTO response = midiaService.register(midiaDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(midiaDTO.getTitle(), response.getTitle());
        assertEquals(midiaDTO.getType(), response.getType());
        
        verify(midiaRepository, times(1)).save(argThat(midia -> 
            midia.getActors().size() == 2 &&
            midia.getActors().get(0).getId().equals(1L) &&
            midia.getActors().get(1).getId().equals(2L)
        ));
    }

    @Test
    void testUpdateMidiaPartialFields() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        midiaDTO.setActorIds(null);

        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        when(midiaRepository.save(any(Midia.class))).thenReturn(midia1);

        MidiaResponseDTO response = midiaService.update(midia1.getId(), midiaDTO);

        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        assertEquals("Updated Title", midia1.getTitle());
        
        assertEquals(Midiatype.MOVIE, midia1.getType());
        assertEquals(1999, midia1.getReleaseYear());
        assertEquals("Wachowski Brothers", midia1.getDirector());
        assertEquals("A computer hacker learns about the true nature of reality", midia1.getSynopsis());
        assertEquals("Sci-Fi", midia1.getGenre());
        assertEquals("matrix.jpg", midia1.getPoseterImageUrl());
        assertEquals(2, midia1.getActors().size());
    }

    @Test
    void testGetAllMidiasPagination() {
        Pageable pageable = PageRequest.of(0, 1); // First page, 1 item per page
        List<Midia> midias = Arrays.asList(midia1);
        Page<Midia> midiaPage = new PageImpl<>(midias, pageable, 2); // Total 2 items
        
        when(midiaRepository.findAll(pageable)).thenReturn(midiaPage);
        
        Page<DetailedMidiaResponseDTO> response = midiaService.getAllMidias(pageable);
        
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(midia1.getId(), response.getContent().get(0).getId());
        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals(0, response.getNumber());
    }

    @Test
    void testUpdateMidiaRemoveAllActors() {
        MidiaUpdateDTO midiaDTO = new MidiaUpdateDTO();
        midiaDTO.setTitle("Updated Title");
        midiaDTO.setActorIds(new ArrayList<>()); // Empty actor list

        when(midiaRepository.findById(midia1.getId())).thenReturn(Optional.of(midia1));
        when(midiaRepository.save(any(Midia.class))).thenReturn(midia1);

        MidiaResponseDTO response = midiaService.update(midia1.getId(), midiaDTO);

        assertNotNull(response);
        assertEquals(midia1.getId(), response.getId());
        assertEquals("Updated Title", midia1.getTitle());
        assertEquals(0, midia1.getActors().size());
    }
}