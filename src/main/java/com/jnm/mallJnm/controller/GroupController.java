package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.CustomerGroup;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.service.CustomerGroupService;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private CustomerGroupService customerGroupService;

    @Autowired
    private CustomerService customerService;

    // 1. 创建客户组
    @PostMapping
    public void createGroup(@Validated @RequestBody CustomerGroup customerGroup) {
        customerGroup.setCreateTime(LocalDateTime.now());
        customerGroup.setUpdateTime(LocalDateTime.now());
        if (customerGroupService.isGroupNameExisted(customerGroup.getName(), null)) {
            throw new ValidatedException(ErrorEnum.HAS_NAME.getCode(), "客户组名称已存在");
        }
        if (!customerGroupService.save(customerGroup)) {
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }

    // 2. 获取客户组详情
    @GetMapping("/{id}")
    public CustomerGroup getGroupById(@PathVariable Integer id) {
        CustomerGroup group = customerGroupService.getById(id);
        if (group == null) {
            throw new ValidatedException(ErrorEnum.NOT_EXIST_ERROR.getCode(), "客户组不存在");
        }
        return group;
    }

    // 3. 更新客户组
    @PutMapping("/{id}")
    public void updateGroup(@PathVariable String id, @Validated @RequestBody CustomerGroup customerGroup) {
        customerGroup.setId(id);
        customerGroup.setUpdateTime(LocalDateTime.now());
        if (customerGroupService.isGroupNameExisted(customerGroup.getName(), id)) {
            throw new ValidatedException(ErrorEnum.HAS_NAME.getCode(), "客户组名称已存在");
        }
        if (!customerGroupService.updateById(customerGroup)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 4. 删除客户组
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteGroup(@PathVariable Integer id) {
        // 检查是否有客户属于这个组
        if (customerService.lambdaQuery().eq(Customer::getGroupId, id).exists()) {
            throw new ValidatedException(ErrorEnum.HAS_CHILD.getCode(), "无法删除：该客户组下仍有客户。请先移除或重新分配客户。");
        }
        if (!customerGroupService.removeById(id)) {
            throw new ServerException(ErrorEnum.DELETE_ERROR);
        }
    }

    // 5. 分页查询客户组列表
    @GetMapping
    public Page<CustomerGroup> listGroups(
            @RequestParam(name = "index", defaultValue = "1") int index,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "name", required = false) String name) {

        Page<CustomerGroup> page = new Page<>(index, size);
        QueryWrapper<CustomerGroup> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isNullOrEmpty(name), "name", name);
        wrapper.orderByDesc("create_time"); // 或者按其他字段排序，如 name, id
        return customerGroupService.page(page, wrapper);
    }

    // 6. 获取所有客户组列表（通常用于下拉选择）
    @GetMapping("/all")
    public List<CustomerGroup> listAllGroups() {
        return customerGroupService.list(new QueryWrapper<CustomerGroup>().orderByAsc("name"));
    }
}