package com.redgear.cog.exception;

public class CogQueryException extends CogException {
    public CogQueryException(String message) {
        super(message);
    }

    public CogQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
