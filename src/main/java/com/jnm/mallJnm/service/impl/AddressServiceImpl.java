package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.AddressMapper;
import com.jnm.mallJnm.model.Address;
import com.jnm.mallJnm.service.AddressService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
}
