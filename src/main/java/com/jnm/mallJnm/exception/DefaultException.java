package com.jnm.mallJnm.exception;

import com.jnm.mallJnm.model.enums.ErrorEnum;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DefaultException extends RuntimeException {
    private int code;
    private String message;

    public DefaultException(){}
    public DefaultException(ErrorEnum errorEnum)  {
        this.code = errorEnum.getCode();
        this.message = errorEnum.getMsg();
    }
    public DefaultException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
