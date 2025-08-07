package com.yohan.bank.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohan.bank.controller.ProductController;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.entity.ClientEntity;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.AccountType;
import com.yohan.bank.exceptions.ClientNotFoundException;
import com.yohan.bank.mapper.ProductMapper;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO request;
    private ProductEntity product;
    private ProductResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new ProductRequestDTO();
        request.setClientId(1L);
        request.setAccountType(AccountType.SAVINGS);
        request.setIsGmfExempt(Boolean.FALSE);

        response = new ProductResponseDTO();
        response.setId(1L);
        response.setAccountType(AccountType.SAVINGS);
        response.setBalance(BigDecimal.valueOf(0.0));
        response.setStatus(AccountStatus.ACTIVE);
        response.setAccountNumber("3312345678");
        response.setClientId(1L);
        response.setIsGmfExempt(false);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        List<ProductResponseDTO> responseList = List.of(response);

        Mockito.when(productService.getAllProducts()).thenReturn(responseList);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$[0].accountNumber").value(response.getAccountNumber()))
                .andExpect(jsonPath("$[0].clientId").value(response.getClientId()))
                .andExpect(jsonPath("$[0].accountType").value(response.getAccountType().toString()))
                .andExpect(jsonPath("$[0].balance").value(response.getBalance()));

        Mockito.verify(productService).getAllProducts();
    }

    @Test
    void getProductById_shouldReturnProduct_whenIdIsValid() throws Exception {
        Long id = 1L;
        Mockito.when(productService.getProductsById(id)).thenReturn(response);

        mockMvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.accountNumber").value(response.getAccountNumber()))
                .andExpect(jsonPath("$.clientId").value(response.getClientId()))
                .andExpect(jsonPath("$.accountType").value(response.getAccountType().toString()))
                .andExpect(jsonPath("$.balance").value(response.getBalance()));

        Mockito.verify(productService).getProductsById(id);
    }



    @Test
    void createProductForClient_shouldReturnCreated() throws Exception {
        Mockito.when(productService.createProductForClient(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/accounts")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.accountNumber").value("3312345678"))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.isGmfExempt").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void updateStatus_shouldReturnNoContent() throws Exception {
        Long productId = 1L;
        AccountStatus newStatus = AccountStatus.INACTIVE;

        mockMvc.perform(patch("/accounts/{productId}/status", productId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).changeAccountStatus(productId, newStatus);
    }
}