package com.redgear.cog.exception;

public abstract class CogException extends RuntimeException {

    public CogException(String message) {
        super(message);
    }

    public CogException(String message, Throwable cause) {
        super(message, cause);
    }

}
