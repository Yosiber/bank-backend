package com.yohan.bank.product.service;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.enums.AccountStatus;
import com.yohan.bank.enums.AccountType;
import com.yohan.bank.exceptions.AccountCancellationNotAllowedException;
import com.yohan.bank.exceptions.DuplicateAccountTypeException;
import com.yohan.bank.exceptions.ProductNotFoundException;
import com.yohan.bank.mapper.ProductMapper;
import com.yohan.bank.repository.ClientRepository;
import com.yohan.bank.repository.ProductRepository;
import com.yohan.bank.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO request;
    private ProductResponseDTO response;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setId(1L);
        product.setAccountType(AccountType.SAVINGS);
        product.setAccountNumber("3312345678");
        product.setIsGmfExempt(false);
        product.setStatus(AccountStatus.ACTIVE);

        request = new ProductRequestDTO();
        request.setClientId(1L);
        request.setAccountType(AccountType.SAVINGS);
        request.setIsGmfExempt(false);

        response = new ProductResponseDTO();
        response.setId(1L);
        response.setAccountType(AccountType.SAVINGS);
        response.setAccountNumber("3312345678");
        response.setBalance(BigDecimal.valueOf(0.0));
        response.setStatus(AccountStatus.ACTIVE);
        response.setIsGmfExempt(false);
    }

    @Test
    void createProduct_shouldCreateProductCorrectly() {
        ProductEntity productToSave = new ProductEntity();
        productToSave.setAccountType(AccountType.SAVINGS);
        productToSave.setIsGmfExempt(false);
        productToSave.setStatus(AccountStatus.ACTIVE);
        productToSave.setAccountNumber("3312345678");

        ProductServiceImpl spyService = Mockito.spy(productService);
        Mockito.doReturn("3312345678").when(spyService).generateAccountNumber(AccountType.SAVINGS);

        Mockito.when(clientRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productMapper.toEntity(request)).thenReturn(productToSave);
        Mockito.when(productRepository.save(productToSave)).thenReturn(product);
        Mockito.when(productMapper.toResponseDto(product)).thenReturn(response);

        ProductResponseDTO result = spyService.createProductForClient(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("3312345678", result.getAccountNumber());
        Assertions.assertEquals(AccountType.SAVINGS, result.getAccountType());
        Assertions.assertEquals(AccountStatus.ACTIVE, result.getStatus());
        Assertions.assertEquals(false, result.getIsGmfExempt());
        Assertions.assertEquals(BigDecimal.valueOf(0.0), result.getBalance());

        Mockito.verify(productMapper).toEntity(request);
        Mockito.verify(productRepository).save(productToSave);
        Mockito.verify(productMapper).toResponseDto(product);
    }

    @Test
    void createProduct_shouldThrowExceptionIfDuplicateAccountType() {
        Mockito.when(clientRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.existsByClientIdAndAccountType(1L, AccountType.SAVINGS)).thenReturn(true);

        Assertions.assertThrows(DuplicateAccountTypeException.class, () -> {
            productService.createProductForClient(request);
        });
    }

    @Test
    void changeAccountStatus_shouldUpdateStatusWhenBalanceIsZero() {
        product.setBalance(BigDecimal.ZERO);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.changeAccountStatus(1L, AccountStatus.CANCELLED);

        Assertions.assertEquals(AccountStatus.CANCELLED, product.getStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void changeAccountStatus_shouldThrowExceptionWhenBalanceIsNotZero() {
        product.setBalance(BigDecimal.valueOf(100.00));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Assertions.assertThrows(AccountCancellationNotAllowedException.class, () -> {
            productService.changeAccountStatus(1L, AccountStatus.CANCELLED);
        });

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void changeAccountStatus_shouldThrowExceptionWhenProductNotFound() {
        Mockito.when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.changeAccountStatus(99L, AccountStatus.CANCELLED);
        });

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void generateAccountNumber_shouldStartWith33ForCurrent() {
        Mockito.when(productRepository.existsByAccountNumber(Mockito.anyString())).thenReturn(false);

        String generated = productService.generateAccountNumber(AccountType.CHECKING);

        Assertions.assertTrue(generated.startsWith("33"));
        Assertions.assertEquals(10, generated.length());
    }

    @Test
    void generateAccountNumber_shouldStartWith53ForSavings() {
        Mockito.when(productRepository.existsByAccountNumber(Mockito.anyString())).thenReturn(false);

        String generated = productService.generateAccountNumber(AccountType.SAVINGS);

        Assertions.assertTrue(generated.startsWith("53"));
        Assertions.assertEquals(10, generated.length());
    }



}
