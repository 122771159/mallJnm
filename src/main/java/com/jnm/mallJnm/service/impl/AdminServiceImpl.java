package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.AdminMapper;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public boolean resetPassword(String id, String newPassword) {
        UpdateWrapper<Admin> wrapper = new UpdateWrapper<>();
        wrapper.set("password", passwordEncoder.encode(newPassword)).eq("id", id);
        return super.update(wrapper);
    }
}