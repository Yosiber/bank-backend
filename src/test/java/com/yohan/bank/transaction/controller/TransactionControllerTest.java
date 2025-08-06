package com.yohan.bank.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohan.bank.controller.TransactionController;
import com.yohan.bank.dto.DepositWithdrawRequestDTO;
import com.yohan.bank.dto.TransactionResponseDTO;
import com.yohan.bank.dto.TransactionTransferResponseDTO;
import com.yohan.bank.dto.TransferRequestDTO;
import com.yohan.bank.enums.TransactionType;
import com.yohan.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepositWithdrawRequestDTO depositRequest;
    private TransactionResponseDTO transactionResponse;
    private TransferRequestDTO transferRequest;
    private TransactionTransferResponseDTO transferResponse;

    @BeforeEach
    void setUp() {
        depositRequest = new DepositWithdrawRequestDTO();
        depositRequest.setAmount(BigDecimal.valueOf(100.0));
        depositRequest.setTransactionType(TransactionType.DEPOSIT);

        transactionResponse = TransactionResponseDTO.builder()
                .id(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100.0))
                .productId(1L)
                .transactionDate(LocalDateTime.now())
                .build();

        transferRequest = new TransferRequestDTO();
        transferRequest.setSourceAccountId(1L);
        transferRequest.setDestinationAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(50.0));

        TransactionResponseDTO debit = TransactionResponseDTO.builder()
                .id(1L)
                .transactionType(TransactionType.WITHDRAW)
                .amount(BigDecimal.valueOf(50.0))
                .productId(1L)
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionResponseDTO credit = TransactionResponseDTO.builder()
                .id(2L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(50.0))
                .productId(2L)
                .transactionDate(LocalDateTime.now())
                .build();

        transferResponse = new TransactionTransferResponseDTO(debit, credit);
    }


    @Test
    void createDepositOrWithdraw_shouldReturnOk() throws Exception {
        Mockito.when(transactionService.createDepositOrWithdraw(eq(1L), any()))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/transaction/deposit-withdraw/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void createTransfer_shouldReturnOk() throws Exception {
        Mockito.when(transactionService.processTransfer(any()))
                .thenReturn(transferResponse);

        mockMvc.perform(post("/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debitTransaction.transactionType").value("WITHDRAW"))
                .andExpect(jsonPath("$.debitTransaction.amount").value(50.0))
                .andExpect(jsonPath("$.debitTransaction.productId").value(1))
                .andExpect(jsonPath("$.creditTransaction.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.creditTransaction.amount").value(50.0))
                .andExpect(jsonPath("$.creditTransaction.productId").value(2));
    }
}
