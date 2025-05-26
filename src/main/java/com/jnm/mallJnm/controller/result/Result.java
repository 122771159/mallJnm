package com.jnm.mallJnm.controller.result;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Result {
    private boolean success;
    private int code;
    private String msg;

    public Result(boolean success, int code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public static Result success() {
        return new Result(true, 0, "成功");
    }

    public static Result fail(int code, String msg) {
        return new Result(false, code, msg);
    }
}
