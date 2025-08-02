package com.yohan.bank.service;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.enums.AccountStatus;

public interface ProductService {

    ProductResponseDTO createProductForClient(ProductRequestDTO productRequestDTO);
    void changeAccountStatus (Long productId, AccountStatus newStatus);
    void validateClientId (Long clientId);
}
