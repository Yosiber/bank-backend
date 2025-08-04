package com.yohan.bank.service;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;

import java.time.LocalDate;


public interface ClientService {

    ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);
    void updateClient(Long id, ClientRequestDTO clientRequestDTO);
    void deleteClientById(Long id);
}
