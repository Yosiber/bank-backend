package com.yohan.bank.service;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO createProductForClient(ProductRequestDTO productRequestDTO);
}
