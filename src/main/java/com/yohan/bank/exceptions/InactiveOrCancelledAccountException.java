package com.yohan.bank.exceptions;

public class InactiveOrCancelledAccountException extends RuntimeException {
    public InactiveOrCancelledAccountException(Long id) {
        super("La cuenta identificada con el numero de cuenta " + id  + " se encuentra inactiva o cancelada ");
    }
    public InactiveOrCancelledAccountException(String message) {
        super(message);
    }
}
