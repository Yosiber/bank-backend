package com.yohan.bank.dto;

import com.yohan.bank.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
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
public class ProductRequestDTO {

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @DecimalMin(value = "0.0", message = "El saldo no puede ser menor a 0")
    private BigDecimal balance;

    @NotNull(message = "Debe especificar si la cuenta es exenta de GMF")
    private Boolean isGmfExempt;

    @NotNull(message = "La id del cliente no puede ser nula")
    private Long clientId;

}
