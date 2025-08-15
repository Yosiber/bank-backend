package com.yohan.bank.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohan.bank.controller.ClientController;
import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.enums.IdentificationType;
import com.yohan.bank.exceptions.ClientNotFoundException;
import com.yohan.bank.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientRequestDTO request;
    private ClientResponseDTO response;

    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;
        request = new ClientRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setIdentificationNumber("123456");
        request.setIdentificationType(IdentificationType.CC);
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        response = new ClientResponseDTO();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setEmail("john.doe@example.com");
        response.setIdentificationNumber("123456");
        response.setIdentificationType(IdentificationType.CC);
        response.setDateOfBirth(LocalDate.of(1990, 1, 1));
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllClients_shouldReturnGEListOfClients() throws Exception {
        List<ClientResponseDTO> responseList = List.of(response);

        Mockito.when(clientService.getAllClients()).thenReturn(responseList);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$[0].firstName").value(response.getFirstName()));

        Mockito.verify(clientService).getAllClients();
    }

    @Test
    void getClientById_shouldReturnClient_whenIdIsValid() throws Exception {
        Long id = 1L;
        Mockito.when(clientService.getClientsById(id)).thenReturn(response);

        mockMvc.perform(get("/clients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()));

        Mockito.verify(clientService).getClientsById(id);
    }

    @Test
    void getClientById_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        Mockito.when(clientService.getClientsById(id)).thenThrow(new ClientNotFoundException(id));

        mockMvc.perform(get("/clients/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(clientService).getClientsById(id);
    }

    @Test
    void createClient_shouldReturnCreatedStatusAndClient() throws Exception {
        Mockito.when(clientService.createClient(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/clients")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.identificationNumber").value("123456"))
                .andExpect(jsonPath("$.identificationType").value("CC"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"));
    }

    @Test
    void updateClient_shouldReturnNoContent() throws Exception {
        ClientRequestDTO requestDTO = new ClientRequestDTO();
        requestDTO.setFirstName("Updated");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("updated.doe@example.com");
        requestDTO.setIdentificationNumber("654321");
        requestDTO.setIdentificationType(IdentificationType.CC);
        requestDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(put("/clients/update/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService).updateClient(eq(1L), Mockito.any());
    }

    @Test
    void deleteClient_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/clients/delete/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService).deleteClientById(1L);
    }

    @Test
    void createClient_shouldReturnBadRequest_whenInvalid() throws Exception {
        ClientRequestDTO requestDTO = new ClientRequestDTO();

        mockMvc.perform(post("/clients")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}

