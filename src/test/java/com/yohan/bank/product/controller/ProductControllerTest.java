package com.yohan.bank.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohan.bank.controller.ProductController;
import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.AccountType;
import com.yohan.bank.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO request;
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