package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.mapper.CustomerMapper;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.ChangePasswordVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl 
    extends ServiceImpl<CustomerMapper, Customer> 
    implements CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @CacheEvict(value = "user", key = "#customerId")
    public boolean disableCustomer(String customerId) {
        return lambdaUpdate()
                .eq(Customer::getId, customerId)
                .set(Customer::getStatus, 0)
                .update();
    }

    @Override
    @CacheEvict(value = "user", key = "#customerId")
    public boolean enableCustomer(String customerId) {
        return lambdaUpdate()
                .eq(Customer::getId, customerId)
                .set(Customer::getStatus, 1)
                .update();
    }

    @Override
    public Customer getByAccount(String account) {
        return lambdaQuery()
                .eq(Customer::getAccount, account)
                .one();
    }

    @Override
    public void settingGroupBatch(List<String> ids, String group_id) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        LambdaUpdateWrapper<Customer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Customer::getId, ids);
        wrapper.set(Customer::getGroupId, group_id);
        this.update(wrapper);
    }

    @Override
    public void removeGroupBatch(List<String> ids, String group_id) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        LambdaUpdateWrapper<Customer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Customer::getId, ids);
        wrapper.eq(Customer::getGroupId, group_id);
        wrapper.set(Customer::getGroupId, null);
        this.update(wrapper);
    }

    @Override
    public Customer getByOpenId(String openId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getOpenid, openId);
        return this.getOne(wrapper);
    }

    @Override
    public boolean updateCustomer(ChangePasswordVO changePasswordVO) {
        User currentUser = SecurityUtils.getCurrentUser();
        String id = currentUser.getId();
        Customer byId = this.getById(id);
        if(changePasswordVO.getOldPassword() != null && !passwordEncoder.encode(changePasswordVO.getOldPassword()).equals(byId.getPassword())){
            throw new ValidatedException(ErrorEnum.PASSWORD_ERROR);
        }
        Customer customer = new Customer();
        customer.setId(id);
        if(changePasswordVO.getNewPassword() != null && changePasswordVO.getOldPassword() != null ){
            customer.setPassword(passwordEncoder.encode(changePasswordVO.getNewPassword()));
        }
        customer.setName(changePasswordVO.getName());
        return updateById(customer);
    }
}