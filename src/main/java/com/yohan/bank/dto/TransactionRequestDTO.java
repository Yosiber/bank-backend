package com.yohan.bank.dto;

import com.yohan.bank.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private BigDecimal amount;
    private TransactionType transactionType;

}
