package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.ProductCategory;
import com.jnm.mallJnm.service.ProductCategoryService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    /**
     * 分页获取商品分类列表
     */
    @GetMapping
    public Page<ProductCategory> list(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "name", required = false) String name) {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isNullOrEmpty(name), "name", name)
                .orderByDesc("sort_order");
        return categoryService.page(new Page<>(page, size), wrapper);
    }

    /**
     * 添加商品分类
     */
    @PostMapping
    public void add(@Validated @RequestBody ProductCategory category) {
        if (!categoryService.save(category)) {
            throw new ServerException("添加分类失败");
        }
    }

    /**
     * 更新商品分类
     */
    @PutMapping("/{id}")
    public void update(@PathVariable String id,
                       @Validated @RequestBody ProductCategory category) {
        category.setId(id);
        if (!categoryService.updateById(category)) {
            throw new ServerException("更新分类失败");
        }
    }

    /**
     * 删除商品分类
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        if (!categoryService.removeById(id)) {
            throw new ServerException("删除分类失败");
        }
    }

    /**
     * 调整分类排序
     */
    @PatchMapping("/{id}/sort")
    public void updateSort(@PathVariable String id,
                           @RequestParam Integer sortOrder) {
        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setSortOrder(sortOrder);
        if (!categoryService.updateById(category)) {
            throw new ServerException("调整排序失败");
        }
    }

    /**
     * 切换分类显示状态
     */
    @PatchMapping("/{id}/status")
    public void toggleStatus(@PathVariable String id) {
        ProductCategory category = categoryService.getById(id);
        if (category == null) {
            throw new ServerException("分类不存在");
        }
        category.setIsShow(!category.getIsShow());
        if (!categoryService.updateById(category)) {
            throw new ServerException("切换状态失败");
        }
    }

    /**
     * 获取所有可见分类（用于下拉选择等场景）
     */
    @GetMapping("/visible")
    public List<ProductCategory> listVisible() {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("is_show", true)
                .orderByDesc("sort_order");
        return categoryService.list(wrapper);
    }
}