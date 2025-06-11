package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.AdminService;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Page<Admin> list(@RequestParam(name = "index", defaultValue = "1") int index,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            @RequestParam(name = "username", required = false) String username,
                            @RequestParam(name = "userType", required = false) String user_type,
                            @RequestParam(name = "status", required = false) String status,
                            @RequestParam(name = "name", required = false) String name
    ) {
        Page<Admin> page = new Page<>(index, size);
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.select("id, username, create_time, user_type,status,name");
        wrapper.like(!StringUtil.isNullOrEmpty(username), "username", username);
        wrapper.like(!StringUtil.isNullOrEmpty(user_type), "user_type", user_type);
        wrapper.eq(!StringUtil.isNullOrEmpty(status), "status", status);
        wrapper.like(!StringUtil.isNullOrEmpty(name), "name", name);
        wrapper.orderByDesc("create_time");
        return adminService.page(page, wrapper);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") String id, @RequestBody Admin admin) {
        admin.setId(id);
        admin.setPassword(null);
        try {
            adminService.updateById(admin);
        } catch (Exception e) {
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
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            adminService.save(admin);
        } catch (Exception e) {
            System.out.println(e);
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }

    @PatchMapping("/resetPassword")
    public void resetPassword(@RequestParam(name = "id") String id,
                              @RequestParam(name = "newPassword") String newPassword) {
        if (!adminService.resetPassword(id, newPassword)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }


    @GetMapping("/sales")
    public Page<Admin> listSales(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username
    ){
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Admin::getId, Admin::getUsername, Admin::getCreateTime);
        wrapper.like(!StringUtil.isNullOrEmpty(username),Admin::getUsername, username);
        wrapper.ne(Admin::getUserType, UserType.SUPER.name());
        return adminService.page(new Page<>(pageNum, pageSize), wrapper);
    }
    @GetMapping("/all")
    public List<Admin> all(){
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Admin::getId, Admin::getUsername, Admin::getCreateTime);
        wrapper.ne(Admin::getUserType, UserType.SUPER.name());
        return adminService.list(wrapper);
    }
    @GetMapping("/getPayUser")
    public List<Customer> getPayUser(@RequestParam(required = false) String keyword){
        User currentUser = SecurityUtils.getCurrentUser();
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Customer::getId, Customer::getAccount, Customer::getName);
        wrapper.like(!StringUtil.isNullOrEmpty(keyword),Customer::getName, keyword)
                .or().like(!StringUtil.isNullOrEmpty(keyword),Customer::getAccount, keyword);
        wrapper.eq(Customer::getAid, currentUser.getId());
        return customerService.list(wrapper);
    }
    @PatchMapping("/{id}/disable")
    public void disable(@PathVariable String id) {
        if (!adminService.disableAdmin(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    @PatchMapping("/{id}/enable")
    public void enable(@PathVariable String id) {
        if (!adminService.enableAdmin(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestBody ChangePasswordVO changePasswordVO) {
        adminService.updatePassword(changePasswordVO);
    }
}
