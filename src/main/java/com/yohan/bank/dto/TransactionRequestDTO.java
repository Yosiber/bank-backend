package com.yohan.bank.dto;

import com.yohan.bank.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long sourceAccountId;

    private Long destinationAccountId;

}
