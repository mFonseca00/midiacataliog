package com.catalog.midiaCatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.catalog.midiacatalog.controller.ActorController;
import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.Actor.ActorRegistrationDTO;
import com.catalog.midiacatalog.dto.Actor.ActorResponseDTO;
import com.catalog.midiacatalog.dto.Midia.MidiaDTO;
import com.catalog.midiacatalog.service.ActorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(ActorController.class)
public class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService actorService;

    private ObjectMapper objectMapper;
    private ActorRegistrationDTO actorRegistrationDTO;
    private ActorResponseDTO actorResponseDTO;
    private ActorDTO actorDTO;
    private MidiaDTO midiaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        actorRegistrationDTO = new ActorRegistrationDTO();
        actorRegistrationDTO.setName("John Doe");
        actorRegistrationDTO.setBirthDate(LocalDate.of(1990, 1, 1));

        actorResponseDTO = new ActorResponseDTO();
        actorResponseDTO.setId(1L);
        actorResponseDTO.setName("John Doe");
        actorResponseDTO.setBirthDate(LocalDate.of(1990, 1, 1));

        actorDTO = new ActorDTO();
        actorDTO.setId(1L);
        actorDTO.setName("John Doe");
        actorDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        actorDTO.setMidias(Arrays.asList());

        midiaDTO = new MidiaDTO();
        midiaDTO.setId(1L);
        midiaDTO.setTitle("Test Movie");
    }

    @Test
    void testRegisterActor() throws Exception {
        when(actorService.register(any(ActorRegistrationDTO.class))).thenReturn(actorResponseDTO);

        mockMvc.perform(post("/actor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actorRegistrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testRemoveActor() throws Exception {
        when(actorService.remove(1L)).thenReturn(actorResponseDTO);

        mockMvc.perform(delete("/actor/remove/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetActor() throws Exception {
        when(actorService.getActor(1L)).thenReturn(actorDTO);

        mockMvc.perform(get("/actor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetAllActors() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActorDTO> actorPage = new PageImpl<>(Arrays.asList(actorDTO), pageable, 1);
        
        when(actorService.getAllActors(any(Pageable.class))).thenReturn(actorPage);

        mockMvc.perform(get("/actor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllActorsWithPagination() throws Exception {
        Pageable pageable = PageRequest.of(1, 5);
        Page<ActorDTO> actorPage = new PageImpl<>(Arrays.asList(actorDTO), pageable, 10);
        
        when(actorService.getAllActors(any(Pageable.class))).thenReturn(actorPage);

        mockMvc.perform(get("/actor/list?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void testAddMidia() throws Exception {
        when(actorService.addMidia(1L, 1L)).thenReturn("Midia added successfully");

        mockMvc.perform(put("/actor/1/add-midia/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Midia added successfully"));
    }

    @Test
    void testRemoveMidia() throws Exception {
        when(actorService.removeMidia(1L, 1L)).thenReturn(midiaDTO);

        mockMvc.perform(delete("/actor/1/remove-midia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetAllActorMidias() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MidiaDTO> midiaPage = new PageImpl<>(Arrays.asList(midiaDTO), pageable, 1);
        
        when(actorService.getAllActorMidias(any(Long.class), any(Pageable.class))).thenReturn(midiaPage);

        mockMvc.perform(get("/actor/1/list-midias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllActorMidiasWithPagination() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<MidiaDTO> midiaPage = new PageImpl<>(Arrays.asList(midiaDTO), pageable, 15);
        
        when(actorService.getAllActorMidias(any(Long.class), any(Pageable.class))).thenReturn(midiaPage);

        mockMvc.perform(get("/actor/1/list-midias?page=0&size=5&sort=title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllActorsWithSorting() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActorDTO> actorPage = new PageImpl<>(Arrays.asList(actorDTO), pageable, 1);
        
        when(actorService.getAllActors(any(Pageable.class))).thenReturn(actorPage);

        mockMvc.perform(get("/actor/list?sort=birthDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }
}