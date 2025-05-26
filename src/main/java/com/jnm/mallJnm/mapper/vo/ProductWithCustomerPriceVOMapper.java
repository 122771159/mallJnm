package com.jnm.mallJnm.mapper.vo;

import com.jnm.mallJnm.mapper.AbstractJoinMapper;
import com.jnm.mallJnm.model.vo.ProductWithCustomerPriceVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductWithCustomerPriceVOMapper extends AbstractJoinMapper<ProductWithCustomerPriceVO> {
}
