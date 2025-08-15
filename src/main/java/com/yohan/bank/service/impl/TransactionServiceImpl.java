package com.yohan.bank.service.impl;

import com.yohan.bank.dto.*;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.entity.TransactionEntity;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.TransactionType;
import com.yohan.bank.exceptions.*;
import com.yohan.bank.mapper.TransactionMapper;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.repository.TransactionRepository;
import com.yohan.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ProductRepository productRepository;

    private static final BigDecimal GMF_RATE = new BigDecimal("0.004");

    @Override
    public List<TransactionResponseDTO> getAllTransaction(){
        List<TransactionEntity> transaction = transactionRepository.findAll();
        return transaction.stream()
                .map(transactionMapper::toResponseDto)
                .toList();
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id){
        TransactionEntity transactionResponseDTO = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id)) ;
        return transactionMapper.toResponseDto(transactionResponseDTO);
    }

    @Override
    public TransactionResponseDTO createDepositOrWithdraw(Long accountId, DepositWithdrawRequestDTO request) {

        ProductEntity product = productRepository.findById(accountId)
                .orElseThrow(() -> new ProductNotFoundException(accountId));

        BigDecimal newBalance;

        if(product.getStatus() == AccountStatus.ACTIVE) {
            newBalance = getBigDecimal(request, product);
        } else {
            throw new InactiveOrCancelledAccountException(accountId);
        }

        if (request.getTransactionType() == TransactionType.WITHDRAW &&
                !Boolean.TRUE.equals(product.getIsGmfExempt()) &&
                request.getAmount().compareTo(BigDecimal.valueOf(1000)) >= 0) {

            BigDecimal gmfAmount = calculateGmf(request.getAmount());
            applyGmf(product, gmfAmount);
        }

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

        if (!AccountStatus.ACTIVE.equals(sourceAccount.getStatus()) ||
                !AccountStatus.ACTIVE.equals(destinationAccount.getStatus())) {
            throw new InactiveOrCancelledAccountException(sourceId);
        }

        BigDecimal amount = request.getAmount();
        BigDecimal gmfAmount = BigDecimal.ZERO;

        if (!Boolean.TRUE.equals(sourceAccount.getIsGmfExempt()) &&
                amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            gmfAmount = calculateGmf(amount);
        }

        BigDecimal totalToDeduct = amount.add(gmfAmount);

        if (sourceAccount.getBalance().compareTo(totalToDeduct) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para cubrir la transferencia y el GMF");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(totalToDeduct));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        if (gmfAmount.compareTo(BigDecimal.ZERO) > 0) {
            TransactionEntity gmfTransaction = TransactionEntity.builder()
                    .amount(gmfAmount)
                    .product(sourceAccount)
                    .transactionType(TransactionType.GMF)
                    .transactionDate(LocalDateTime.now())
                    .build();
            transactionRepository.save(gmfTransaction);
        }

        productRepository.save(sourceAccount);
        productRepository.save(destinationAccount);

        TransactionEntity debitTx = TransactionEntity.builder()
                .amount(amount)
                .product(sourceAccount)
                .transactionType(TransactionType.WITHDRAW)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionEntity creditTx = TransactionEntity.builder()
                .amount(amount)
                .product(destinationAccount)
                .transactionType(TransactionType.DEPOSIT)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(debitTx);
        transactionRepository.save(creditTx);

        return new TransactionTransferResponseDTO(
                transactionMapper.toResponseDto(debitTx),
                transactionMapper.toResponseDto(creditTx)
        );
    }



    private BigDecimal calculateGmf(BigDecimal amount) {
        return amount.multiply(GMF_RATE).setScale(2, RoundingMode.HALF_UP);
    }


    private void applyGmf(ProductEntity account, BigDecimal gmfAmount) {
        account.setBalance(account.getBalance().subtract(gmfAmount));
        productRepository.save(account);

        TransactionEntity gmfTransaction = TransactionEntity.builder()
                .amount(gmfAmount)
                .product(account)
                .transactionType(TransactionType.GMF)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(gmfTransaction);
    }

    private  BigDecimal getBigDecimal(DepositWithdrawRequestDTO request, ProductEntity product) {
        BigDecimal newBalance = product.getBalance();
        BigDecimal amount = request.getAmount();

        switch (request.getTransactionType()) {
            case TRANSFER -> throw new UnsupportedOperationException("Las transferencias no están permitidas en esta dirección");

            case DEPOSIT -> newBalance = newBalance.add(amount);

            case WITHDRAW -> {
                BigDecimal gmf = BigDecimal.ZERO;

                if (!Boolean.TRUE.equals(product.getIsGmfExempt()) && amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                    gmf = calculateGmf(amount);
                }

                BigDecimal totalRequired = amount.add(gmf);
                if (product.getBalance().compareTo(totalRequired) < 0) {
                    throw new InsufficientBalanceException("Saldo insuficiente para cubrir el retiro y el GMF");
                }

                newBalance = newBalance.subtract(amount);
            }

            case GMF -> throw new UnsupportedOperationException("GMF no es permitido aquí");
        }

        return newBalance;
    }

}
