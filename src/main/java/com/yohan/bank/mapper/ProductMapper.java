package com.yohan.bank.mapper;

import com.yohan.bank.dto.ProductRequestDTO;
import com.yohan.bank.dto.ProductResponseDTO;
import com.yohan.bank.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    @Mapping(target = "client.id", source = "clientId")
    ProductEntity toEntity(ProductRequestDTO dto);
    ProductResponseDTO toResponseDto(ProductEntity entity);
}
