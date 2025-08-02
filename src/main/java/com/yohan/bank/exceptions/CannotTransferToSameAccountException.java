package com.yohan.bank.exceptions;

public class CannotTransferToSameAccountException extends RuntimeException {
    public CannotTransferToSameAccountException() {
        super("No se puede transferir a la misma cuenta.");
    }

    public CannotTransferToSameAccountException(String message) {
        super(message);
    }
}
