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

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id){
        ProductResponseDTO productResponseDTO = productService.getProductsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDTO);
    }

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
