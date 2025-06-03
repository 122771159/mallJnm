package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.mapper.OneSalesCustomerVOMapper;
import com.jnm.mallJnm.mapper.vo.CustomerVOMapper;
import com.jnm.mallJnm.model.Admin;
import com.jnm.mallJnm.model.SalesCustomer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.CustomerVO;
import com.jnm.mallJnm.model.vo.OneSaleCustomersVO;
import com.jnm.mallJnm.mybatisplus.wrapper.JoinWrapper;
import com.jnm.mallJnm.service.AdminService;
import com.jnm.mallJnm.service.SalesCustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OneSalesCustomerVOMapper oneSalesCustomerVOMapper;
    @Autowired
    private CustomerVOMapper customerVOMapper;
    @Autowired
    private SalesCustomerService salesCustomerService;
    @GetMapping
    public Page<Admin> list(@RequestParam(name = "index", defaultValue = "1") int index,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            @RequestParam(name = "username", required = false) String username,
                            @RequestParam(name = "userType", required = false) String user_type
    ) {
        Page<Admin> page = new Page<>(index, size);
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.select("id, username, create_time, user_type");
        wrapper.like(!StringUtil.isNullOrEmpty(username), "username", username);
        wrapper.like(!StringUtil.isNullOrEmpty(user_type), "user_type", user_type);
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

    @GetMapping("/sales-customers")
    public Page<OneSaleCustomersVO> listSalesCustomers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String aid
    ) {
        JoinWrapper<OneSaleCustomersVO> wrapper = new JoinWrapper<>();
        wrapper.alias("sc");
        wrapper.select("sc.id,c.name,c.account,sc.create_time as createTime,sc.update_time as updateTime");
        wrapper.like(!StringUtil.isNullOrEmpty(name), "c.name", name);
        wrapper.like(!StringUtil.isNullOrEmpty(account), "c.account", account);
        wrapper.eq("sc.aid", aid);
        wrapper.leftJoin("jnm_customer as c on sc.cid = c.id");
        return oneSalesCustomerVOMapper.selectJoinPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @GetMapping("/no-sales-customers")
    public Page<CustomerVO> listNoSalesCustomers(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String account,
                                                 @RequestParam(required = false) String aid) {
        JoinWrapper<CustomerVO> wrapper = new JoinWrapper<>();
        wrapper.alias("c");
        wrapper.select("c.id,c.name,c.account");
        wrapper.like(!StringUtil.isNullOrEmpty(name), "c.name", name);
        wrapper.like(!StringUtil.isNullOrEmpty(account), "c.account", account);
        wrapper.and(qw -> qw.isNull("sc.aid") // 条件1: 客户没有关联的业务员 (sc.aid IS NULL)
                .or()                 // 或者
                .ne("sc.aid", aid)); // 条件2: 客户关联的业务员不是指定的aid (sc.aid <> aid)
        wrapper.leftJoin("jnm_sales_customer as sc on c.id = sc.cid");
        return customerVOMapper.selectJoinPage(new Page<>(pageNum, pageSize), wrapper);
    }
    @PostMapping("/sales-customers/batch/{aid}")
    public void addSalesCustomers(@RequestBody ArrayList<String> cids, @PathVariable String aid) {
        salesCustomerService.addSaleCustomerBatch(cids, aid);
    }
    @PutMapping("/sales-customers/batch/{aid}")
    public void removeSalesCustomers(@RequestBody ArrayList<String> ids, @PathVariable String aid) {
        salesCustomerService.removeSaleCustomerBatch(ids, aid);
    }
    @DeleteMapping("/sales-customers")
    public void removeGroup(
            @RequestParam String aid,
            @RequestParam String id
    ) {
        LambdaUpdateWrapper<SalesCustomer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SalesCustomer::getId, id);
        wrapper.eq(SalesCustomer::getAid, aid);
        salesCustomerService.remove(wrapper);
    }
}
