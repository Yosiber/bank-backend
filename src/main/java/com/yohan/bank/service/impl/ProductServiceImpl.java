package com.yohan.bank.service.impl;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.exceptions.ClientNotFoundException;
import com.yohan.bank.exceptions.DuplicateAccountTypeException;
import com.yohan.bank.mapper.ProductMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final  ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO createProductForClient(ProductRequestDTO productRequestDTO) {
        validateClientId(productRequestDTO.getClientId());

        boolean exists = productRepository.existsByClientIdAndAccountType(
                productRequestDTO.getClientId(),
                productRequestDTO.getAccountType()
        );

        if (exists) {
            throw new DuplicateAccountTypeException(
                    productRequestDTO.getClientId(),
                    productRequestDTO.getAccountType()
            );
        }

        ProductEntity entity = productMapper.toEntity(productRequestDTO);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDto(saved);
    }

    private void validateClientId(Long clientId) {
        if (clientId == null || !clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }


}
