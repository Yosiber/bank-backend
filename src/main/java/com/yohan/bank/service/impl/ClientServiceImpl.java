package com.yohan.bank.service.impl;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.entity.ClientEntity;
import com.yohan.bank.exceptions.*;
import com.yohan.bank.mapper.ClientMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        ClientEntity entity = clientMapper.toEntity(dto);
        validateAge(entity.getDateOfBirth());
        ClientEntity saved = clientRepository.save(entity);
        return clientMapper.toResponseDto(saved);
    }

    @Override
    public void updateClient(Long id, ClientRequestDTO clientRequestDTO) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        boolean exists = clientRepository.existsByIdentificationNumberAndIdNot(
                clientRequestDTO.getIdentificationNumber(), id);

        if (exists) {
            throw new DuplicateClientIdentificationException();
        }

        clientMapper.updateEntityFromDto(clientRequestDTO, client);
        validateAge(client.getDateOfBirth());
        clientRepository.save(client);
    }


    @Override
    public void deleteClientById(Long id) {

        if (!clientRepository.existsById(id)) {
            throw new ClientNotFoundException(id);
        }

        boolean hasProducts = productRepository.existsByClientId(id);

        if (hasProducts) {
            throw new ClientHaveProductException(id);
        }

        clientRepository.deleteById(id);
    }


    private void validateAge(LocalDate dateOfBirth) {

        LocalDate today = LocalDate.now();
        Period age = Period.between(dateOfBirth, today);

        if (age.getYears() < 18) {
            throw new UnderageClientException();
        }
    }


}
