package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.mapper.vo.CustomerVOMapper;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;
import com.jnm.mallJnm.model.vo.CustomerVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.mybatisplus.wrapper.JoinWrapper;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerVOMapper customerVOMapper;


    // 1. 分页查询客户（主路径）
    @GetMapping
    public Page<CustomerVO> pageQuery(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String aid,
            @RequestParam(required = false) Integer status) {
        User currentUser = SecurityUtils.getCurrentUser();

        JoinWrapper<CustomerVO> joinWrapper = new JoinWrapper<>();
        joinWrapper.alias("c");
        joinWrapper.select("c.id, c.name, c.account, c.status, c.create_time as createTime," +
                "c.update_time as updateTime,c.group_id as groupId,g.name as groupName,a.username,c.aid");
        joinWrapper.like(!StringUtil.isNullOrEmpty(name), "c.name", name);
        joinWrapper.like(!StringUtil.isNullOrEmpty(account), "c.account", account);
        joinWrapper.like(!StringUtil.isNullOrEmpty(groupId), "c.group_id", groupId);
        if(currentUser.getUserType().equals(UserType.SALES.name())){
            joinWrapper.eq("c.aid", currentUser.getId());
        }else{
            joinWrapper.eq(!StringUtil.isNullOrEmpty(aid), "c.aid", aid);
        }
        joinWrapper.eq(status != null, "c.status", status);
        joinWrapper.leftJoin("jnm_customer_group as g on g.id = c.group_id");
        joinWrapper.leftJoin("jnm_admin as a on c.aid = a.id");
        joinWrapper.orderByDesc("c.create_time");
        return customerVOMapper.selectJoinPage(new Page<>(pageNum, pageSize), joinWrapper);
    }
    @PutMapping("/update_info")
    public void updateInfo(@RequestBody Customer customer) {
        if(customer.getId() == null){

        }
    }
    // 2. 创建客户
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public void create(@RequestBody @Validated Customer customer) {
        User currentUser = SecurityUtils.getCurrentUser();
        String userType = currentUser.getUserType();
        try {
            customer.setId(null);
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            if(userType.equals(UserType.SALES.name())){
                customer.setAid(currentUser.getId());
            }else if(userType.equals(UserType.SUPER.name())){
                if(StringUtil.isNullOrEmpty(customer.getAid())){
                    throw new ServerException(ErrorEnum.AID_EMPTY);
                }
            }else if(userType.equals(UserType.ADMIN.name())){
                if(StringUtil.isNullOrEmpty(customer.getAid())){
                    customer.setAid(currentUser.getId());
                }
            }
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

    @GetMapping("/notBoundCustomers")
    public Page<Customer> listNotBoundCustomers(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String account) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!StringUtil.isNullOrEmpty(account),Customer::getAccount, account);
        queryWrapper.eq(!StringUtil.isNullOrEmpty(name),Customer::getName, name);
        queryWrapper.eq(Customer::getAid, "").or().isNull(Customer::getAid);
        return customerService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @PostMapping("/boundCustomer/batch/{aid}")
    public void addSalesCustomers(@RequestBody ArrayList<String> cids, @PathVariable String aid) {
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Customer::getId, cids);
        updateWrapper.set(Customer::getAid, aid);
        customerService.update(updateWrapper);
    }

    /**
     * 根据aid 获取 绑定的客户
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param account
     * @param aid
     * @return
     */
    @GetMapping("/boundedCustomer")
    public Page<CustomerVO> listBoundedCustomer(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String aid
    ) {
        JoinWrapper<CustomerVO> wrapper = new JoinWrapper<>();
        wrapper.alias("sc");
        wrapper.select("sc.id,sc.name,sc.account,sc.create_time as createTime,sc.update_time as updateTime");
        wrapper.like(!StringUtil.isNullOrEmpty(name), "c.name", name);
        wrapper.like(!StringUtil.isNullOrEmpty(account), "c.account", account);
        wrapper.eq("sc.aid", aid);
        wrapper.leftJoin("jnm_admin as a on sc.aid = a.id");
        return customerVOMapper.selectJoinPage(new Page<>(pageNum, pageSize), wrapper);
    }


    @PutMapping("/removeBoundCustomer/batch")
    public void removeBoundCustomerBatch(@RequestBody ArrayList<String> ids) {
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Customer::getId, ids);
        updateWrapper.set(Customer::getAid, null);
        customerService.update(updateWrapper);
    }

    @DeleteMapping("/removeBoundCustomer")
    public void removeBoundCustomer(
            @RequestParam String id
    ) {
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getId, id);
        updateWrapper.set(Customer::getAid, null);
        customerService.update(updateWrapper);
    }
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestBody ChangePasswordVO changePasswordVO) {
        customerService.updateCustomer(changePasswordVO);
    }
}