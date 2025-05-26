package com.jnm.mallJnm.model.vo;

import com.jnm.mallJnm.model.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UsersVo extends Users {
    private Object detail;
}
