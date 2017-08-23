package com.redgear.cog.exception;

public class CogRestClientException extends CogException {
    public CogRestClientException(String message) {
        super(message);
    }

    public CogRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
