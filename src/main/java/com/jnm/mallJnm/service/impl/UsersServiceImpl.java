package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.mapper.UsersMapper;
import com.jnm.mallJnm.model.Users;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean updatePassword(String id, String password, String newPassword) {
        Users old = getById(id);
        if (old != null) {
            if (!passwordEncoder.matches(password, old.getPassword())){
                throw new ValidatedException(ErrorEnum.PASSWORD_ERROR);
            }
            UpdateWrapper<Users> wrapper = new UpdateWrapper<>();
            wrapper.set("password", passwordEncoder.encode(newPassword));
            wrapper.eq("id", id);
            return super.update(wrapper);
        }
        return false;
    }


}
