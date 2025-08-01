package com.yohan.bank.exceptions;

import java.time.LocalDate;

public class UnderageClientException extends RuntimeException {

    public UnderageClientException() {
        super("El cliente debe tener al menos 18 años.");
    }

    public UnderageClientException(String message) {
        super(message);
    }
}
