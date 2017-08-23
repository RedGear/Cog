package com.redgear.cog.exception;

public class CogRuntimeException extends CogException {
    public CogRuntimeException(String message) {
        super(message);
    }

    public CogRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
