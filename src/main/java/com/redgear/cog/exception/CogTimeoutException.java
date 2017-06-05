package com.redgear.cog.exception;

public class CogTimeoutException extends CogException {
    public CogTimeoutException(String message) {
        super(message);
    }

    public CogTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
