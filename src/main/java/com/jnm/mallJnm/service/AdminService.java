package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;


public interface AdminService extends IService<Admin> {

    boolean resetPassword(String id, String newPassword);
    boolean updatePassword(ChangePasswordVO changePasswordVO);
    Admin getSalesByOpenId(String username);

    boolean disableAdmin(String id);
    boolean enableAdmin(String id);
}