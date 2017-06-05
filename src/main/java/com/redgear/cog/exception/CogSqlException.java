package com.redgear.cog.exception;


import java.sql.SQLException;

public class CogSqlException extends CogException {

    public CogSqlException(String message) {
        super(message);
    }

    public CogSqlException(String message, SQLException cause) {
        super(message, cause);
    }

    public CogSqlException(SQLException cause) {
        super(cause.getMessage(), cause);
    }

}
