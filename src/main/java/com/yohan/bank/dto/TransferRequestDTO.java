package com.yohan.bank.dto;


import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class TransferRequestDTO extends TransactionRequestDTO   {
    private Long sourceAccountId;
    private Long destinationAccountId;
}