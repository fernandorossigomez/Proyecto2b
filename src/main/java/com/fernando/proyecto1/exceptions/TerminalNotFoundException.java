package com.fernando.proyecto1.exceptions;

public class TerminalNotFoundException extends RuntimeException {

    public TerminalNotFoundException(String message) {
        super(message);
    }
}