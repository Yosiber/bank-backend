package com.yohan.bank.exceptions;

public class DuplicateClientIdentificationException extends RuntimeException {

    public DuplicateClientIdentificationException() {
        super("Este numero de identificación ya se encuentra registrado : ");
    }
    public DuplicateClientIdentificationException(String message) {
        super(message);
    }
}
