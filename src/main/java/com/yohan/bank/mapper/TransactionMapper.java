package com.yohan.bank.mapper;

import com.yohan.bank.dto.TransactionRequestDTO;
import com.yohan.bank.dto.TransactionResponseDTO;
import com.yohan.bank.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    @Mapping(target = "sourceAccount.id", source = "sourceAccountId")
    @Mapping(target = "destinationAccount.id", source = "destinationAccountId")
    TransactionEntity toEntity(TransactionRequestDTO dto);
    TransactionResponseDTO toResponseDto(TransactionEntity entity);
}
