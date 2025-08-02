package com.yohan.bank.controller;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProductForClient(@RequestBody @Valid ProductRequestDTO account) {
        ProductResponseDTO productResponseDTO = productService.createProductForClient(account) ;
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductResponseDTO> updateStatus( @PathVariable Long productId, @RequestBody @Valid AccountStatus status) {
        productService.changeAccountStatus(productId, status);
        return ResponseEntity.noContent().build();
    }
}
