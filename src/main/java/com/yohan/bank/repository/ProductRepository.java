package com.yohan.bank.repository;

import com.yohan.bank.entity.ProductEntity;
import com.yohan.bank.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByClientIdAndAccountType(Long clientId, AccountType accountType);
    boolean existsByClientId(Long clientId);
    boolean existsByAccountNumber(String fullnumber);

    Long id(Long id);
}
