package com.yohan.bank.mapper;

import com.yohan.bank.dto.TransactionRequestDTO;
import com.yohan.bank.dto.TransactionResponseDTO;
import com.yohan.bank.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    TransactionEntity toEntity(TransactionRequestDTO dto);

    @Mapping(source = "transactionType", target = "type")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "sourceAccount.id", target = "sourceAccount")
    @Mapping(source = "destinationAccount.id", target = "destinationAccount")
    TransactionResponseDTO toResponseDto(TransactionEntity entity);
}
