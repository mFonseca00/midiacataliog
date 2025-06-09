package com.catalog.midiaCatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        List<ActorDTO> actors = Arrays.asList(actorDTO);
        when(actorService.getAllActors()).thenReturn(actors);

        mockMvc.perform(get("/actor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
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

        mockMvc.perform(put("/actor/1/remove-midia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetAllActorMidias() throws Exception {
        List<MidiaDTO> midias = Arrays.asList(midiaDTO);
        when(actorService.getAllActorMidias(1L)).thenReturn(midias);

        mockMvc.perform(get("/actor/1/list-midias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }
}