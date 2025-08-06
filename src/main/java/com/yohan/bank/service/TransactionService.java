package com.yohan.bank.service;

import com.yohan.bank.dto.DepositWithdrawRequestDTO;
import com.yohan.bank.dto.TransactionResponseDTO;
import com.yohan.bank.dto.TransactionTransferResponseDTO;
import com.yohan.bank.dto.TransferRequestDTO;

public interface TransactionService {

    TransactionResponseDTO createDepositOrWithdraw(Long accountId, DepositWithdrawRequestDTO request);
    TransactionTransferResponseDTO processTransfer(TransferRequestDTO request);
}
