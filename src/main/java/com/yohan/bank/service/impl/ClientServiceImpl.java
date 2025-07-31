package com.yohan.bank.service.impl;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.entity.ClientEntity;
import com.yohan.bank.mapper.ClientMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        ClientEntity entity = clientMapper.toEntity(dto);
        ClientEntity saved = clientRepository.save(entity);
        return clientMapper.toResponseDto(saved);
    }

}
