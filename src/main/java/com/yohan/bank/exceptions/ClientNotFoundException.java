package com.yohan.bank.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Id del usuario no encontrada : " + id);
    }


    public ClientNotFoundException(String message) {
        super(message);
    }
}
