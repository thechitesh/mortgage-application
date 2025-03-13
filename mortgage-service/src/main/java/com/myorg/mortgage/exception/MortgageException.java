package com.myorg.mortgage.exception;

public class MortgageException extends RuntimeException {

    public MortgageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
