package com.yohan.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.yohan.bank.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class DepositWithdrawRequestDTO extends TransactionRequestDTO  {

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @DecimalMin(value = "0.0", message = "El saldo no puede ser menor a 0")
    private BigDecimal amount;

    @NotNull(message = "La id del cliente no puede ser nula")
    private Long product;



}
