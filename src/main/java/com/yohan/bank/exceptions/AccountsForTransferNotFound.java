package com.yohan.bank.exceptions;

public class AccountsForTransferNotFound extends RuntimeException {

    public AccountsForTransferNotFound() {
        super("Ambas cuentas deben especificarse para una transferencia.");
    }


    public AccountsForTransferNotFound(String message) {
        super(message);
    }
}
