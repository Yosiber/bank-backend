package com.yohan.bank.client.service;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.entity.ClientEntity;
import com.yohan.bank.exceptions.ClientHaveProductException;
import com.yohan.bank.exceptions.ClientNotFoundException;
import com.yohan.bank.mapper.ClientMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientEntity client;
    private ClientEntity existingEntity;
    private ClientRequestDTO requestDTO;
    private ClientResponseDTO responseDTO;
    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;

        client = new ClientEntity();
        client.setId(id);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setDateOfBirth(LocalDate.of(2000, 1, 1));

        requestDTO = new ClientRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        requestDTO.setIdentificationNumber("1234567890");

        responseDTO = new ClientResponseDTO();
        responseDTO.setId(id);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");

        existingEntity = new ClientEntity();
        existingEntity.setId(id);
        existingEntity.setFirstName("Old");
        existingEntity.setLastName("Name");
        existingEntity.setDateOfBirth(LocalDate.of(1995, 1, 1));
        existingEntity.setIdentificationNumber("9999999999");
    }

    @Test
    void getAllClients_shouldReturnListOfClientResponseDTOs() {
        List<ClientEntity> clients = List.of(client);
        Mockito.when(clientRepository.findAll()).thenReturn(clients);
        Mockito.when(clientMapper.toResponseDto(client)).thenReturn(responseDTO);

        List<ClientResponseDTO> result = clientService.getAllClients();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(responseDTO, result.getFirst());

        Mockito.verify(clientRepository).findAll();
        Mockito.verify(clientMapper).toResponseDto(client);
    }

    @Test
    void getClientsById_shouldReturnClientResponseDTO_whenClientExists() {

        Mockito.when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Mockito.when(clientMapper.toResponseDto(client)).thenReturn(responseDTO);


        ClientResponseDTO result = clientService.getClientsById(id);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(responseDTO, result);

        Mockito.verify(clientRepository).findById(id);
        Mockito.verify(clientMapper).toResponseDto(client);
    }

    @Test
    void getClientsById_shouldThrowException_whenClientNotFound() {

        Mockito.when(clientRepository.findById(id)).thenReturn(Optional.empty());


        Assertions.assertThrows(ClientNotFoundException.class, () -> clientService.getClientsById(id));
        Mockito.verify(clientRepository).findById(id);
        Mockito.verifyNoInteractions(clientMapper);
    }

    @Test
    void createClient_shouldSaveClientAndReturnResponse() {
        ClientEntity entityToSave = new ClientEntity();
        entityToSave.setFirstName("John");
        entityToSave.setLastName("Doe");
        entityToSave.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Mockito.when(clientMapper.toEntity(requestDTO)).thenReturn(entityToSave);
        Mockito.when(clientRepository.save(entityToSave)).thenReturn(client);
        Mockito.when(clientMapper.toResponseDto(client)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.createClient(requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("John", result.getFirstName());
        Assertions.assertEquals("Doe", result.getLastName());

        Mockito.verify(clientMapper).toEntity(requestDTO);
        Mockito.verify(clientRepository).save(entityToSave);
        Mockito.verify(clientMapper).toResponseDto(client);
    }

    @Test
    void updateClient_shouldUpdateClientCorrectly() {
        Mockito.when(clientRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.when(clientRepository.existsByIdentificationNumberAndIdNot("1234567890", id)).thenReturn(false);

        clientService.updateClient(id, requestDTO);

        Mockito.verify(clientRepository).findById(id);
        Mockito.verify(clientRepository).existsByIdentificationNumberAndIdNot("1234567890", id);
        Mockito.verify(clientMapper).updateEntityFromDto(requestDTO, existingEntity);
        Mockito.verify(clientRepository).save(existingEntity);
    }

    @Test
    void deleteClientById_shouldDeleteClient_WhenNoProductsExist() {
        Mockito.when(clientRepository.existsById(id)).thenReturn(true);
        Mockito.when(productRepository.existsByClientId(id)).thenReturn(false);

        clientService.deleteClientById(id);

        Mockito.verify(clientRepository).existsById(id);
        Mockito.verify(productRepository).existsByClientId(id);
        Mockito.verify(clientRepository).deleteById(id);
    }

    @Test
    void deleteClientById_shouldThrowClientNotFoundException_WhenClientDoesNotExist() {
        Mockito.when(clientRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(id));

        Mockito.verify(clientRepository).existsById(id);
        Mockito.verify(productRepository, Mockito.never()).existsByClientId(Mockito.anyLong());
        Mockito.verify(clientRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteClientById_shouldThrowClientHaveProductException_WhenClientHasProducts() {
        Mockito.when(clientRepository.existsById(id)).thenReturn(true);
        Mockito.when(productRepository.existsByClientId(id)).thenReturn(true);

        Assertions.assertThrows(ClientHaveProductException.class, () -> clientService.deleteClientById(id));

        Mockito.verify(clientRepository).existsById(id);
        Mockito.verify(productRepository).existsByClientId(id);
        Mockito.verify(clientRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
