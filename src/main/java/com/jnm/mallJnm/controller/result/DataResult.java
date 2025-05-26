package com.jnm.mallJnm.controller.result;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DataResult<T> extends Result{
    private T data;

    public DataResult(){
        super(true, 0, "成功");
    }

    public DataResult(T data) {
        super(true, 0, "成功");
        this.data = data;
    }
}
