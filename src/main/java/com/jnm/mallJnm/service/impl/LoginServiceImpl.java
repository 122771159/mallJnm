package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.service.AdminService;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.service.LoginService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@CacheConfig(cacheNames = {"user"})
@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    AdminService adminService;
    @Autowired
    CustomerService customerService;
    @CachePut(key = "#result.id")
    @Override
    public User loadByAccountType(String account, String userType, String openId) throws AuthenticationException {
        if (StringUtils.isBlank(userType)) {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
        if(!StringUtils.isBlank(openId)){
            clearOpenId(openId);
        }
        if (userType.equals(UserType.CUSTOMER.name())) {
            QueryWrapper<Customer> usersQueryWrapper = new QueryWrapper<>();
            usersQueryWrapper.eq("account", account);
            Customer users = customerService.getOne(usersQueryWrapper);
            if (users == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            users.setOpenid(openId);
            customerService.updateById(users);
            return new User(users.getId(), users.getAccount(), users.getName(), users.getPassword(),
                    userType, listUserPermissions(userType), users.getStatus().equals(1));
        }
        throw new InternalAuthenticationServiceException("用户类型错误");
    }

    @Override
    @CachePut(key = "#result.id")
    public User loadByAccount(String account,String openId) {
        QueryWrapper<Admin> adminWrapper = new QueryWrapper<>();
        adminWrapper.eq("username", account);
        if(!StringUtils.isBlank(openId)){
            clearOpenId(openId);
        }
        Admin admin = adminService.getOne(adminWrapper);
        if (admin == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        admin.setOpenid(openId);
        adminService.updateById(admin);
        return new User(admin.getId(), admin.getUsername(), admin.getName(), admin.getPassword(),
                admin.getUserType(), listUserPermissions(admin.getUserType()), true);
    }

    @Cacheable(key = "#id")
    @Override
    public User loadById(String id, String userType) {
        if (StringUtils.isBlank(userType)) {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
        if (userType.equals(UserType.ADMIN.name()) || userType.equals(UserType.SALES.name()) || userType.equals(UserType.SUPER.name())) {
            Admin admin = adminService.getById(id);
            if (admin == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(admin.getId(), admin.getUsername(), admin.getName(), admin.getPassword(),
                    userType, listUserPermissions(userType), true); // 管理员没有这个字段
        }else{
            Customer users = customerService.getById(id);
            if (users == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(users.getId(), users.getAccount(), users.getName(), users.getPassword(),
                    userType, listUserPermissions(userType), users.getStatus().equals(1));
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public User loadByOpenId(String openId, String userType) {
        if (StringUtils.isBlank(userType)) {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
        if (userType.equals(UserType.ADMIN.name()) || userType.equals(UserType.SALES.name()) || userType.equals(UserType.SUPER.name())) {
            Admin admin = adminService.getSalesByOpenId(openId);
            if (admin == null) {
                throw new UsernameNotFoundException("微信未绑定此账号");
            }
            return new User(admin.getId(), admin.getUsername(), admin.getName(), admin.getPassword(),
                    userType, listUserPermissions(userType), admin.getStatus());
        }else{
            Customer users = customerService.getByOpenId(openId);
            if (users == null) {
                throw new UsernameNotFoundException("微信未绑定此账号");
            }
            return new User(users.getId(), users.getAccount(), users.getName(), users.getPassword(),
                    userType, listUserPermissions(userType), users.getStatus().equals(1));
        }
    }

    private List<GrantedAuthority> listUserPermissions(String userType) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_" + userType));
        return list;
    }
    private void clearOpenId(String openId){
        LambdaUpdateWrapper<Admin> adminLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        adminLambdaUpdateWrapper.set(Admin::getOpenid,null);
        adminLambdaUpdateWrapper.eq(Admin::getOpenid, openId);
        adminService.update(adminLambdaUpdateWrapper);
        LambdaUpdateWrapper<Customer> customerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        customerLambdaUpdateWrapper.set(Customer::getOpenid,null);
        customerLambdaUpdateWrapper.eq(Customer::getOpenid, openId);
        customerService.update(customerLambdaUpdateWrapper);
    }
}
