package com.yohan.bank.service.impl;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.AccountType;
import com.yohan.bank.exceptions.AccountCancellationNotAllowedException;
import com.yohan.bank.exceptions.ClientNotFoundException;
import com.yohan.bank.exceptions.DuplicateAccountTypeException;
import com.yohan.bank.exceptions.ProductNotFoundException;
import com.yohan.bank.mapper.ProductMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Random random = new Random();


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
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setAccountNumber(generateAccountNumber(entity.getAccountType()));
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDto(saved);
    }

    @Override
    public void changeAccountStatus(Long productId, AccountStatus newStatus) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

       if(newStatus == AccountStatus.CANCELLED && product.getBalance().compareTo(BigDecimal.ZERO) != 0 ) {
           throw new AccountCancellationNotAllowedException(productId);
       }
        product.setStatus(newStatus);
        productRepository.save(product);
    }


    private void validateClientId(Long clientId) {
        if (clientId == null || !clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }

    public String generateAccountNumber(AccountType type) {
        String prefix = type == AccountType.SAVINGS ? "53" : "33";
        String suffix;
        String fullNumber;
        do {
            suffix = String.format("%08d", random.nextInt(100_000_000));
            fullNumber = prefix + suffix;
        } while (productRepository.existsByAccountNumber(fullNumber));

        return fullNumber;
    }

}
