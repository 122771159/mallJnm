package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.util.StringUtil;
import com.jnm.mallJnm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping
    public Page<Admin> list(@RequestParam(name = "index", defaultValue = "1") int index,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            @RequestParam(name = "username", required = false) String username) {
        Page<Admin> page = new Page<>(index, size);
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.select("id, username, user_type, create_time");
        wrapper.like(!StringUtil.isNullOrEmpty(username), "username", username);
        wrapper.orderByDesc("create_time");
        return adminService.page(page, wrapper);
    }
    @PutMapping("/{id}")
    public void update(@PathVariable("id") String id, @RequestBody Admin admin) {
        admin.setId(id);
        admin.setPassword(null);
        try {
            adminService.updateById(admin);
        }catch (Exception e){
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        adminService.removeById(id);
    }
    @PostMapping
    public void add(@RequestBody Admin admin) {
        try {
            admin.setId(null);
//            admin.setUserType(UserType.ADMIN.name());
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            adminService.save(admin);
        }catch (Exception e){
            System.out.println(e);
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }
    @PatchMapping("/resetPassword")
    public void resetPassword(@RequestParam(name = "id") String id,
                              @RequestParam(name = "newPassword") String newPassword){
        if (!adminService.resetPassword(id, newPassword)){
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }
}
