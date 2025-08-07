package com.yohan.bank.service;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;

import java.util.List;


public interface ClientService {


    List<ClientResponseDTO> getAllClients();


    ClientResponseDTO getClientsById(Long id);

    ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);
    void updateClient(Long id, ClientRequestDTO clientRequestDTO);
    void deleteClientById(Long id);
}
