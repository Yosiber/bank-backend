package com.yohan.bank.dto;

import com.yohan.bank.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @Pattern(regexp = "^(33|53)\\d{8}$", message = "El número de cuenta debe tener 10 dígitos y empezar con 33 o 53")
    private String accountNumber;

    @DecimalMin(value = "0.0", message = "El saldo no puede ser menor a 0")
    private BigDecimal balance;

    @NotNull(message = "Debe especificar si la cuenta es exenta de GMF")
    private Boolean isGmfExempt;

    @NotBlank(message = "La id del cliente no puede ser un campo vacío")
    private Long clientId;

}
