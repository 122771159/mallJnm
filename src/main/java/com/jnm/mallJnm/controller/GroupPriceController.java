package com.jnm.mallJnm.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.mapper.vo.ProductWithGroupPriceVOMapper;
import com.jnm.mallJnm.model.GroupProductPrice;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.ProductWithGroupPriceVO;
import com.jnm.mallJnm.mybatisplus.wrapper.JoinWrapper;
import com.jnm.mallJnm.service.GroupProductPriceService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/group_price")
public class GroupPriceController {
    @Autowired
    private ProductWithGroupPriceVOMapper productWithGroupPriceVOMapper;
    @Autowired
    private GroupProductPriceService groupProductPriceService;

    @GetMapping
    public Page<ProductWithGroupPriceVO> list(@RequestParam(name = "index", defaultValue = "1") int index,
                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                              @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "gid", required = false) String gid,
                                              @RequestParam(name = "code", required = false) String code) {
        if (StringUtils.isEmpty(gid)) {
            throw new ServerException(ErrorEnum.NOT_EXIST_ERROR);
        }
        JoinWrapper<ProductWithGroupPriceVO> joinWrapper = new JoinWrapper<>();
        joinWrapper.alias("gp")
                .select("p.name as productName, p.id as productId, p.code as productCode," +
                        "p.description as description, p.images as mainImage, p.price as standardPrice," +
                        "p.stock as stock, gp.custom_price as customPrice,gp.id")
                .eq(!StringUtil.isNullOrEmpty(name), "p.name", name)
                .eq(!StringUtil.isNullOrEmpty(code), "p.code", code)
                .eq("gp.group_id", gid)
                .leftJoin("jnm_product as p on gp.product_id = p.id");
        Page<ProductWithGroupPriceVO> page = new Page<>(index, size);
        return productWithGroupPriceVOMapper.selectJoinPage(page, joinWrapper);
    }

    @PostMapping
    public void save(@RequestBody GroupProductPrice groupProductPrice) {
        if(groupProductPriceService.isGroupProductPriceExisted(groupProductPrice.getGroupId(), groupProductPrice.getProductId(), null)){
            throw new ServerException(ErrorEnum.REPEAT);
        }
        groupProductPrice.setUpdateTime(LocalDateTime.now());
        groupProductPrice.setCreateTime(LocalDateTime.now());
        groupProductPriceService.save(groupProductPrice);

    }

    @PutMapping
    public void update(@RequestBody GroupProductPrice groupProductPrice) {
        groupProductPrice.setUpdateTime(LocalDateTime.now());
        groupProductPriceService.updateById(groupProductPrice);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        groupProductPriceService.removeById(id);
    }
}
