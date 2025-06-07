package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Address;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.AddressService;
import com.jnm.mallJnm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/{cid}")
    public void add(@PathVariable String cid,@RequestBody @Validated Address address) {
        judgeCid(cid);
        address.setCid(cid);
        address.setCreateTime(LocalDateTime.now());
        if(address.getIsDefault().equals(1)){
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Address::getIsDefault,0);
            updateWrapper.eq(Address::getCid,cid);
            addressService.update(updateWrapper);
        }
        addressService.save(address);
    }
    @GetMapping("/{cid}")
    public List<Address> list(@PathVariable String cid) {
        judgeCid(cid);
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getCid, cid);
        queryWrapper.orderByDesc(Address::getIsDefault);
        queryWrapper.orderByAsc(Address::getCreateTime);
        return addressService.list(queryWrapper);
    }
    @PutMapping("/{cid}")
    public void update(@PathVariable String cid,@RequestBody @Validated Address address) {
        judgeCid(cid);
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getCid, cid);
        updateWrapper.eq(Address::getId, address.getId());
        if(address.getIsDefault().equals(1)){
            LambdaUpdateWrapper<Address> updateWrapper1 = new LambdaUpdateWrapper<>();
            updateWrapper1.set(Address::getIsDefault,0);
            updateWrapper1.eq(Address::getCid,cid);
            addressService.update(updateWrapper1);
        }
        addressService.update(address, updateWrapper);
    }

    @DeleteMapping("/{cid}/{id}")
    public void delete(@PathVariable String id, @PathVariable String cid) {
        judgeCid(cid);
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getId, id);
        updateWrapper.set(Address::getCid, cid);
        addressService.remove(updateWrapper);
    }
    @PutMapping("/setDefault/{cid}")
    public void setDefault(@PathVariable String cid,@RequestParam String address_id) {
        judgeCid(cid);
        LambdaUpdateWrapper<Address> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.set(Address::getIsDefault,0);
        updateWrapper1.eq(Address::getCid,cid);
        addressService.update(updateWrapper1); // 全部设为0
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Address::getIsDefault,1);
        updateWrapper.eq(Address::getCid,cid);
        updateWrapper.eq(Address::getId,address_id);
        addressService.update(updateWrapper);// 全部设为1
    }
    @GetMapping("/default/{cid}")
    public Address getDefault(@PathVariable String cid){
        judgeCid(cid);
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getIsDefault,1);
        queryWrapper.eq(Address::getCid,cid);
        return addressService.getOne(queryWrapper);
    }
    private void judgeCid(String cid){
        User user = SecurityUtils.getCurrentUser();
        String userType = user.getUserType();
        if(!userType.equals(UserType.CUSTOMER.name())){
            // 判断cid 的业务员是不是操作人的id
            Customer byId = customerService.getById(cid);
            if(byId == null){
                throw new ServerException(ErrorEnum.CID_EMPTY);
            }
            if(!byId.getAid().equals(user.getId())){
                throw new ServerException(ErrorEnum.IDENTITY_ERROR);
            }
        }else{
            if(!user.getId().equals(cid)){
                throw new ServerException(ErrorEnum.IDENTITY_ERROR);
            }
        }
    }
}
