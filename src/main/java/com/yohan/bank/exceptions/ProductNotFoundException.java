package com.yohan.bank.exceptions;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(Long id) {
    super("Id de la cuenta no encontrada : " + id);
  }

  public ProductNotFoundException(String message) {
        super(message);
    }
}
