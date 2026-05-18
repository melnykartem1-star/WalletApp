package org.my.walletapp.exception;

public class IdenticalPasswordsException extends RuntimeException {
    public IdenticalPasswordsException(String message) {super(message);}
}
