package com.yohan.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionTransferResponseDTO {
    private TransactionResponseDTO debitTransaction;
    private TransactionResponseDTO creditTransaction;
}