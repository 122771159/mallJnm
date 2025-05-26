package com.jnm.mallJnm.exception;

import com.jnm.mallJnm.model.enums.ErrorEnum;


public class ValidatedException extends DefaultException{
    public ValidatedException() {}

    public ValidatedException(ErrorEnum errorEnum) {
        this(errorEnum.getCode(), errorEnum.getMsg());
    }

    public ValidatedException(int code, String massage) {
        super(code, massage);
    }

    public ValidatedException(String msg) {
        this(-1, msg);
    }
}
