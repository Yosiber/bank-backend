package com.yohan.bank.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("El saldo de la cuenta es insuficiente.");
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
