package com.jnm.mallJnm.model.vo;

import lombok.Data;

@Data
public class ChangePasswordVO {
    private String name;
    private String oldPassword;
    private String newPassword;
}
