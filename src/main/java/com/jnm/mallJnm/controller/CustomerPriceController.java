package com.jnm.mallJnm.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.mapper.vo.ProductWithCustomerPriceVOMapper;
import com.jnm.mallJnm.model.CustomerProductPrice;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.ProductWithCustomerPriceVO;
import com.jnm.mallJnm.mybatisplus.wrapper.JoinWrapper;
import com.jnm.mallJnm.service.CustomerProductPriceService;
import com.jnm.mallJnm.util.StringUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("/customer_price")
public class CustomerPriceController {
    @Autowired
    private  ProductWithCustomerPriceVOMapper productWithCustomerPriceVOMapper;
    @Autowired
    private CustomerProductPriceService customerProductPriceService;
    @GetMapping
    public Page<ProductWithCustomerPriceVO> list(@RequestParam(name = "index", defaultValue = "1") int index,
                     @RequestParam(name = "size", defaultValue = "10") int size,
                     @RequestParam(name = "name", required = false) String name,
                     @RequestParam(name = "cid", required = false) String cid,
                     @RequestParam(name = "code", required = false) String code){
        if(StringUtils.isEmpty(cid)){
            throw new ServerException(ErrorEnum.NOT_EXIST_ERROR);
        }
        JoinWrapper<ProductWithCustomerPriceVO> joinWrapper = new JoinWrapper<>();
        joinWrapper.alias("cp")
                    .select("p.name as productName, p.id as productId, p.code as productCode," +
                            "p.description as description, p.images as mainImage, p.price as standardPrice," +
                            "p.stock as stock, cp.custom_price as customPrice,cp.id")
                    .eq(!StringUtil.isNullOrEmpty(name), "p.name", name)
                    .eq(!StringUtil.isNullOrEmpty(code), "p.code", code)
                    .eq("cp.customer_id", cid)
                    .leftJoin("jnm_product as p on cp.product_id = p.id");
        Page<ProductWithCustomerPriceVO> page = new Page<>(index, size);
        return productWithCustomerPriceVOMapper.selectJoinPage(page, joinWrapper);
    }
    @PostMapping
    public void save(@RequestBody CustomerProductPrice customerProductPrice){
        // TODO 检验是否重复等等
           customerProductPriceService.save(customerProductPrice);

    }
    @PutMapping
    public void update(@RequestBody CustomerProductPrice customerProductPrice){
        // TODO 检验是否重复等等
        customerProductPriceService.updateById(customerProductPrice);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        customerProductPriceService.removeById(id);
    }

}
