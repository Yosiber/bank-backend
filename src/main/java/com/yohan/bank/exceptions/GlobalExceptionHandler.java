package com.yohan.bank.exceptions;

import com.yohan.bank.enums.IdentificationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {


        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put(TIMESTAMP, LocalDateTime.now());
        errors.put(STATUS, HttpStatus.BAD_REQUEST.value());
        errors.put(ERROR, "Bad Request");

        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    err.put("field", error.getField());
                    err.put(MESSAGE, error.getDefaultMessage());
                    return err;
                })
                .toList();

        errors.put("errors", fieldErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getCause();
        String message;

        if (rootCause instanceof IllegalArgumentException && ex.getMessage().contains("Cannot deserialize value of type")) {
            String allowedValues = Arrays.stream(IdentificationType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            message = "Valor inválido para 'identificationType'. Valores permitidos: " + allowedValues;
        } else {
            message = "Error al procesar el cuerpo de la solicitud: " + ex.getMessage();
        }

        Map<String, Object> error = new LinkedHashMap<>();
        error.put(TIMESTAMP, LocalDateTime.now());
        error.put(STATUS, HttpStatus.BAD_REQUEST.value());
        error.put(ERROR, "Error de lectura del cuerpo JSON");
        error.put(MESSAGE, message);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Object>  handleClientNotFoundException(ClientNotFoundException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Cliente no encontrado", ex.getMessage());
    }

    @ExceptionHandler(UnderageClientException.class)
    public ResponseEntity<Object> handleUnderageClientException(UnderageClientException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Edad inválida", ex.getMessage());

    }

    @ExceptionHandler(DuplicateClientIdentificationException.class)
    public ResponseEntity<Object> handleDuplicateClientIdentificationException(DuplicateClientIdentificationException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Cliente existente", ex.getMessage());
    }

    @ExceptionHandler(DuplicateAccountTypeException.class)
    public ResponseEntity<Object> handleDuplicateAccountTypeException(DuplicateAccountTypeException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Cuenta existente", ex.getMessage());
    }

    @ExceptionHandler(ClientHaveProductException.class)
    public ResponseEntity<Object> handleClientHaveProductException(ClientHaveProductException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Producto asociado al cliente", ex.getMessage());
    }


    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, error);
        body.put(MESSAGE, message);
        return new ResponseEntity<>(body, status);
    }

}
