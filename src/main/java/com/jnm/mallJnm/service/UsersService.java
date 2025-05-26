package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Users;

public interface UsersService extends IService<Users> {
    boolean updatePassword(String id, String password, String newPassword);

}
