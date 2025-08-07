package com.yohan.bank.exceptions;

public class TransactionNotFoundException extends RuntimeException {

  public TransactionNotFoundException(Long id) {
    super("Id de la transacción no encontrada : " + id);
  }
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
