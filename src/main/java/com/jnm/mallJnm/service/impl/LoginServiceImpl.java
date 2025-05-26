package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jnm.mallJnm.mapper.AdminMapper;
import com.jnm.mallJnm.mapper.UsersMapper;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.Users;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.service.LoginService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
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

    @Resource
    UsersMapper usersMapper;
    @Resource
    AdminMapper adminMapper;

    @CachePut(key = "#userType + #result.id")
    @Override
    public User loadByAccount(String account, String userType) throws AuthenticationException {
        if (StringUtils.isBlank(userType)) {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
        if (userType.equals("ADMIN")) {
            QueryWrapper<Admin> adminWrapper = new QueryWrapper<>();
            adminWrapper.eq("username", account);
            Admin admin = adminMapper.selectOne(adminWrapper);
            if (admin == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(admin.getId(), admin.getUsername(), "", admin.getPassword(),
                    userType, listUserPermissions(userType), true);
        }
        else if(userType.equals("CUSTOMER")){
            QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
            usersQueryWrapper.eq("username", account);
            Users users = usersMapper.selectOne(usersQueryWrapper);
            if (users == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(users.getId(), users.getUsername(), "", users.getPassword(),
                    userType, listUserPermissions(userType), true);
        }
        else {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
    }

    @Cacheable(key = "#userType + #id")
    @Override
    public User loadById(String id, String userType) {
        if (StringUtils.isBlank(userType)) {
            throw new InternalAuthenticationServiceException("用户类型错误");
        }
        if (userType.equals("ADMIN")) {
            Admin admin = adminMapper.selectById(id);
            if (admin == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(admin.getId(), admin.getUsername(), "", admin.getPassword(),
                    userType, listUserPermissions(userType), true);
        }
        if (userType.equals("CUSTOMER")) {
            Users users = usersMapper.selectById(id);
            if (users == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return new User(users.getId(), users.getUsername(), "", users.getPassword(),
                    userType, listUserPermissions(userType), true);
        }
        return null;
    }


    private List<GrantedAuthority> listUserPermissions(String userType) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_" + userType));
        return list;
    }
}
