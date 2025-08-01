package com.yohan.bank.exceptions;

public class ClientHaveProductException extends RuntimeException {

    public ClientHaveProductException(Long id) {
        super("El usuario con Id: " + id + "Tiene una cuenta registrada ");
    }
    public ClientHaveProductException(String message) {
        super(message);
    }
}
