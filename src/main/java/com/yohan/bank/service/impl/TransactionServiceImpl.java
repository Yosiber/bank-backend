package com.yohan.bank.service.impl;

import com.yohan.bank.dto.*;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.entity.TransactionEntity;
import com.yohan.bank.enums.TransactionType;
import com.yohan.bank.exceptions.CannotTransferToSameAccountException;
import com.yohan.bank.exceptions.InsufficientBalanceException;
import com.yohan.bank.exceptions.ProductNotFoundException;
import com.yohan.bank.mapper.TransactionMapper;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.repository.TransactionRepository;
import com.yohan.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ProductRepository productRepository;

    @Override
    public TransactionResponseDTO createDepositOrWithdraw(Long accountId, DepositWithdrawRequestDTO request) {

        ProductEntity product = productRepository.findById(accountId)
                .orElseThrow(() -> new ProductNotFoundException(accountId));

        BigDecimal newBalance = getBigDecimal(request, product);

        TransactionEntity transaction = transactionMapper.toEntity(request);
        transaction.setProduct(product);

        product.setBalance(newBalance);
        productRepository.save(product);

        TransactionEntity newTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponseDto(newTransaction);
    }

    @Override
    public TransactionTransferResponseDTO processTransfer(TransferRequestDTO request) {

        Long sourceId = request.getSourceAccountId();
        Long destinationId = request.getDestinationAccountId();

        if (sourceId.equals(destinationId)) {
            throw new CannotTransferToSameAccountException();
        }

        ProductEntity sourceAccount = productRepository.findById(sourceId)
                .orElseThrow(() -> new ProductNotFoundException(sourceId));

        ProductEntity destinationAccount = productRepository.findById(destinationId)
                .orElseThrow(() -> new ProductNotFoundException(destinationId));

        BigDecimal amount = request.getAmount();

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        productRepository.save(sourceAccount);
        productRepository.save(destinationAccount);

        TransactionEntity debitTx = TransactionEntity.builder()
                .amount(amount)
                .product(sourceAccount)
                .transactionType(TransactionType.WITHDRAW)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();

        TransactionEntity creditTx = TransactionEntity.builder()
                .amount(amount)
                .product(destinationAccount)
                .transactionType(TransactionType.DEPOSIT)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();

        transactionRepository.save(debitTx);
        transactionRepository.save(creditTx);

        return new TransactionTransferResponseDTO(
                transactionMapper.toResponseDto(debitTx),
                transactionMapper.toResponseDto(creditTx)
        );
    }

    private static BigDecimal getBigDecimal(DepositWithdrawRequestDTO request, ProductEntity product) {
        BigDecimal newBalance = product.getBalance();
        BigDecimal amount = request.getAmount();

        if (request.getTransactionType().equals(TransactionType.DEPOSIT)) {
            newBalance = newBalance.add(amount);
        } else if (request.getTransactionType().equals(TransactionType.WITHDRAW)) {
            if (product.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException();
            }
            newBalance = newBalance.subtract(amount);
        }

        return newBalance;
    }
}
