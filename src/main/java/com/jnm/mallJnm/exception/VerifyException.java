package com.jnm.mallJnm.exception;

import org.springframework.security.core.AuthenticationException;


public class VerifyException extends AuthenticationException {
    public VerifyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VerifyException(String msg) {
        super(msg);
    }
}
