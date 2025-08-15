package com.yohan.bank.controller;

import com.yohan.bank.dto.*;
import com.yohan.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> transactionResponseDTO = transactionService.getAllTransaction();
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }
    @PostMapping("/deposit-withdraw/{accountId}")
    public ResponseEntity<TransactionResponseDTO> createDepositOrWithdraw(
            @PathVariable Long accountId,
            @RequestBody DepositWithdrawRequestDTO request) {

        TransactionResponseDTO response = transactionService.createDepositOrWithdraw(accountId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionTransferResponseDTO> createTransfer(@RequestBody TransferRequestDTO request) {

        TransactionTransferResponseDTO response = transactionService.processTransfer(request);
        return ResponseEntity.ok(response);
    }


}
