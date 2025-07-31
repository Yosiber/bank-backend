package com.yohan.bank.service;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;


public interface ClientService {

    ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);

}
