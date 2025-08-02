package com.yohan.bank.exceptions;

public class AccountCancellationNotAllowedException extends RuntimeException {

    public AccountCancellationNotAllowedException(Long id) {
        super("La cuenta identificada con el numero: " + id  + " no puede ser cancelada porque su saldo es superior a 0 ");
    }
    public AccountCancellationNotAllowedException(String message) {
        super(message);
    }
}
