package com.yohan.bank.dto;

import com.yohan.bank.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    @DecimalMin(value = "0.0", message = "El saldo no puede ser menor a 0")
    private BigDecimal amount;
    private TransactionType transactionType;

}
