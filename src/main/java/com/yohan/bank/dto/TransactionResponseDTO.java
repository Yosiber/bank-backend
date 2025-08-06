package com.yohan.bank.dto;

import com.yohan.bank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
     private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long sourceAccount;
    private Long destinationAccount;
    private Long productId;
    private LocalDateTime transactionDate;


}
