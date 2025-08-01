package com.yohan.bank.exceptions;

import com.yohan.bank.enums.AccountType;

public class DuplicateAccountTypeException extends RuntimeException {

    public DuplicateAccountTypeException(Long clientId, AccountType accountType) {
        super("El cliente ya tiene registrada este tipo de cuenta.");
    }

    public DuplicateAccountTypeException(String message) {
        super(message);
    }
}
