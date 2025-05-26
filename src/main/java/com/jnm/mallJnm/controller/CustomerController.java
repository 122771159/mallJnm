package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. 分页查询客户（主路径）
    @GetMapping
    public Page<Customer> pageQuery(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) Integer status) {
        
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.select("id, name, account, status, create_time, update_time");
        wrapper.like(!StringUtil.isNullOrEmpty(name), "name", name);
        wrapper.like(!StringUtil.isNullOrEmpty(account), "account", account);
        wrapper.eq(status != null, "status", status);
        wrapper.orderByDesc("create_time");
        
        return customerService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    // 2. 创建客户
    @PostMapping
    public void create(@RequestBody Customer customer) {
        try {
            customer.setId(null);
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customer.setStatus(1);
            customerService.save(customer);
        } catch (Exception SQLIntegrityConstraintViolationException) {
            throw new ServerException(201,"登录账号重复");
        }
    }

    // 3. 获取客户详情
    @GetMapping("/{id}")
    public Customer getById(@PathVariable String id) {
         Customer customer = customerService.getById(id);
         customer.setPassword(null);
         return customer;
    }

    // 4. 更新客户
    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            customer.setPassword(null);
            customer.setAccount(null);
            customerService.updateById(customer);
        } catch (Exception e) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 5. 删除客户
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if (!customerService.removeById(id)) {
            throw new ServerException(ErrorEnum.DELETE_ERROR);
        }
    }

    // 6. 批量删除
    @DeleteMapping("/batch")
    public void batchDelete(@RequestBody List<String> ids) {
        if (!customerService.removeByIds(ids)) {
            throw new ServerException(ErrorEnum.DELETE_ERROR);
        }
    }

    // 7. 禁用客户
    @PatchMapping("/{id}/disable")
    public void disable(@PathVariable String id) {
        if (!customerService.disableCustomer(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 8. 启用客户
    @PatchMapping("/{id}/enable")
    public void enable(@PathVariable String id) {
        if (!customerService.enableCustomer(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 9. 重置密码
    @PatchMapping("/{id}/reset-password")
    public void resetPassword(
            @PathVariable String id,
            @RequestParam String newPassword) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setPassword(passwordEncoder.encode(newPassword));
        
        if (!customerService.updateById(customer)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 10. 检查账号是否存在
    @GetMapping("/check-account")
    public boolean checkAccountExist(@RequestParam String account) {
        return customerService.lambdaQuery()
                .eq(Customer::getAccount, account)
                .exists();
    }
}