package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Product;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.vo.ProductVO;
import com.jnm.mallJnm.service.ProductService;
import com.jnm.mallJnm.service.UploadFileService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UploadFileService uploadFileService;
    // 分页查询商品列表
    @GetMapping
    public Page<Product> list(
            @RequestParam(name = "index", defaultValue = "1") int index,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "categoryId", required = false) Integer categoryId) {
        
        Page<Product> page = new Page<>(index, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.select("*");
        wrapper.like(!StringUtil.isNullOrEmpty(name), "name", name);
        wrapper.eq(categoryId != null, "category_id", categoryId);
        wrapper.orderByDesc("create_time");
        return productService.page(page, wrapper);
    }

    // 添加商品
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public void add(@RequestBody ProductVO productVO) {
        try {
            Product product = new Product();
            BeanUtils.copyProperties(productVO, product);
            product.setId(null);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            uploadFileService.setUsed(productVO.getUsedImages(),true);
            productService.save(product);
        } catch (Exception e) {
            System.out.println(e);
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }

    // 更新商品
    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public void update(@PathVariable("id") Long id, @RequestBody ProductVO productVO) {
        try {
            Product product = new Product();
            BeanUtils.copyProperties(productVO, product);
            product.setId(id);
            product.setUpdateTime(LocalDateTime.now());
            uploadFileService.setUsed(productVO.getUsedImages(),true);
            uploadFileService.setUsed(productVO.getNoUsedImages(),false);
            productService.updateById(product);

        } catch (Exception e) {
            throw new ServerException(ErrorEnum.SAVE_ERROR);
        }
    }

    // 删除商品
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public void delete(@PathVariable("id") Long id,@RequestBody ProductVO productVO) {
        uploadFileService.setUsed(productVO.getNoUsedImages(),false);
        productService.removeById(id);
    }

    // 商品上架
    @PatchMapping("/{id}/publish")
    public void publish(@PathVariable("id") Long id) {
        if (!productService.publishProduct(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }

    // 商品下架
    @PatchMapping("/{id}/unpublish")
    public void unpublish(@PathVariable("id") Long id) {
        if (!productService.unpublishProduct(id)) {
            throw new ServerException(ErrorEnum.UPDATE_ERROR);
        }
    }
}