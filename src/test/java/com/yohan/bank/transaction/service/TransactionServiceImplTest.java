package com.yohan.bank.transaction.service;

import com.yohan.bank.dto.DepositWithdrawRequestDTO;
import com.yohan.bank.dto.TransactionResponseDTO;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.entity.TransactionEntity;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.AccountType;
import com.yohan.bank.enums.TransactionType;
import com.yohan.bank.exceptions.InactiveOrCancelledAccountException;
import com.yohan.bank.exceptions.TransactionNotFoundException;
import com.yohan.bank.mapper.TransactionMapper;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.repository.TransactionRepository;
import com.yohan.bank.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private ProductEntity product;
    private DepositWithdrawRequestDTO request;
    private TransactionEntity transaction;
    private TransactionResponseDTO response;
    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;

        product = new ProductEntity();
        product.setId(1L);
        product.setAccountNumber("3312345678");
        product.setAccountType(AccountType.SAVINGS);
        product.setIsGmfExempt(false);
        product.setBalance(BigDecimal.valueOf(3000));
        product.setStatus(AccountStatus.ACTIVE);

        request = new DepositWithdrawRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(2000));

        transaction = new TransactionEntity();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(2000));
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setProduct(product);

        response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(2000));
        response.setTransactionType(TransactionType.DEPOSIT);
    }

    @Test
    void getAllTransaction_shouldReturnListOfTransactionResponseDTOs() {
        List<TransactionEntity> transactions = List.of(transaction);
        Mockito.when(transactionRepository.findAll()).thenReturn(transactions);
        Mockito.when(transactionMapper.toResponseDto(transaction)).thenReturn(response);

        List<TransactionResponseDTO> result = transactionService.getAllTransaction();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(response, result.getFirst());

        Mockito.verify(transactionRepository).findAll();
        Mockito.verify(transactionMapper).toResponseDto(transaction);
    }

    @Test
    void getTransactionById_shouldReturnTransactionResponseDTO_whenClientExists() {

        Mockito.when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        Mockito.when(transactionMapper.toResponseDto(transaction)).thenReturn(response);


        TransactionResponseDTO result = transactionService.getTransactionById(id);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(response, result);

        Mockito.verify(transactionRepository).findById(id);
        Mockito.verify(transactionMapper).toResponseDto(transaction);
    }

    @Test
    void getTransactionById_shouldThrowException_whenTransactionNotFound() {

        Mockito.when(transactionRepository.findById(id)).thenReturn(Optional.empty());


        Assertions.assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionById(id));
        Mockito.verify(transactionRepository).findById(id);
        Mockito.verifyNoInteractions(transactionMapper);
    }

    @Test
    void createDeposit_shouldUpdateBalanceAndReturnTransaction() {
        request = new DepositWithdrawRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(5000));

        product.setBalance(BigDecimal.ZERO);
        product.setIsGmfExempt(true);
        product.setStatus(AccountStatus.ACTIVE);

        response.setAmount(BigDecimal.valueOf(5000));
        response.setTransactionType(TransactionType.DEPOSIT);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(transactionMapper.toEntity(request)).thenReturn(transaction);
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(transaction);
        Mockito.when(transactionMapper.toResponseDto(transaction)).thenReturn(response);

        TransactionResponseDTO result = transactionService.createDepositOrWithdraw(1L, request);

        Assertions.assertEquals(BigDecimal.valueOf(5000), product.getBalance());
        Assertions.assertEquals(TransactionType.DEPOSIT, result.getTransactionType());

        Mockito.verify(productRepository).save(product);
        Mockito.verify(transactionRepository).save(Mockito.any());
    }

    @Test
    void createWithdraw_shouldApplyGmfAndUpdateBalance() {
        request.setTransactionType(TransactionType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(2000));

        product.setBalance(BigDecimal.valueOf(3000));
        product.setIsGmfExempt(false);
        product.setStatus(AccountStatus.ACTIVE);

        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(BigDecimal.valueOf(1000)); // Se espera que reste GMF

        response.setTransactionType(TransactionType.WITHDRAW);
        response.setAmount(BigDecimal.valueOf(1000));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(transactionMapper.toEntity(request)).thenReturn(transaction);
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(transaction);
        Mockito.when(transactionMapper.toResponseDto(transaction)).thenReturn(response);

        TransactionResponseDTO result = transactionService.createDepositOrWithdraw(1L, request);

        Mockito.verify(productRepository, Mockito.times(2)).save(Mockito.any());

        Assertions.assertEquals(0, result.getAmount().compareTo(BigDecimal.valueOf(1000)));
    }

    @Test
    void createTransaction_shouldThrowIfAccountInactive() {
        product.setStatus(AccountStatus.INACTIVE);

        request = new DepositWithdrawRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(1000));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Assertions.assertThrows(InactiveOrCancelledAccountException.class, () ->
                transactionService.createDepositOrWithdraw(1L, request)
        );
    }

    @Test
    void createWithdraw_shouldNotApplyGmf_whenAmountIsLessThan1000() {
        request.setTransactionType(TransactionType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(500));

        product.setBalance(BigDecimal.valueOf(1000));
        product.setIsGmfExempt(false);
        product.setStatus(AccountStatus.ACTIVE);

        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(BigDecimal.valueOf(500));

        response.setTransactionType(TransactionType.WITHDRAW);
        response.setAmount(BigDecimal.valueOf(500));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(transactionMapper.toEntity(request)).thenReturn(transaction);
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(transaction);
        Mockito.when(transactionMapper.toResponseDto(transaction)).thenReturn(response);

        TransactionResponseDTO result = transactionService.createDepositOrWithdraw(1L, request);

        Mockito.verify(productRepository, Mockito.times(1)).save(Mockito.any());
        Assertions.assertEquals(0, result.getAmount().compareTo(BigDecimal.valueOf(500)));
    }
}
