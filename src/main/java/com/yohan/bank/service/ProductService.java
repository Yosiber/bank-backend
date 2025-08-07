package com.yohan.bank.service;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.enums.AccountStatus;

import java.util.List;

public interface ProductService {


    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductsById(Long id);

    ProductResponseDTO createProductForClient(ProductRequestDTO productRequestDTO);
    void changeAccountStatus (Long productId, AccountStatus newStatus);
}
