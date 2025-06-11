package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.mapper.AdminMapper;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

    @Override
    public boolean updatePassword(ChangePasswordVO changePasswordVO) {
        User currentUser = SecurityUtils.getCurrentUser();
        String id = currentUser.getId();
        Admin byId = this.getById(id);
        if(changePasswordVO.getOldPassword() != null && !passwordEncoder.encode(changePasswordVO.getOldPassword()).equals(byId.getPassword())){
            throw new ValidatedException(ErrorEnum.PASSWORD_ERROR);
        }
        Admin admin = new Admin();
        admin.setId(id);
        if(changePasswordVO.getNewPassword() != null && changePasswordVO.getOldPassword() != null ){
            admin.setPassword(passwordEncoder.encode(changePasswordVO.getNewPassword()));
        }
        admin.setName(changePasswordVO.getName());
        return updateById(admin);
    }

    @Override
    public Admin getSalesByOpenId(String openId) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getOpenid, openId);
        return getOne(wrapper);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public boolean disableAdmin(String id) {
        return lambdaUpdate()
                .eq(Admin::getId, id)
                .set(Admin::getStatus, 0)
                .update();
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public boolean enableAdmin(String id) {
        return lambdaUpdate()
                .eq(Admin::getId, id)
                .set(Admin::getStatus, 1)
                .update();
    }


}