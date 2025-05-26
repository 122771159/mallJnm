package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Admin;


public interface AdminService extends IService<Admin> {

    boolean resetPassword(String id, String newPassword);
}