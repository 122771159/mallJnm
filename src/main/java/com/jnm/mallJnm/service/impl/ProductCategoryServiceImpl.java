package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.mapper.ProductCategoryMapper;
import com.jnm.mallJnm.model.ProductCategory;
import com.jnm.mallJnm.service.ProductCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {


}