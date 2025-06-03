package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.Product;
import com.jnm.mallJnm.model.ProductCategory;
import com.jnm.mallJnm.model.vo.CategoryWithProductsVO;
import com.jnm.mallJnm.model.vo.ProductDisplayVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.service.PriceCalculationService;
import com.jnm.mallJnm.service.ProductCategoryService;
import com.jnm.mallJnm.service.ProductService;
import com.jnm.mallJnm.util.WechatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wx")
public class WxController {
    @Autowired
    private WechatUtil wechatUtil;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceCalculationService priceCalculationService;
    @Autowired
    private CustomerService customerService;
    @GetMapping("/products")
    public List<CategoryWithProductsVO> getCategoriesWithProducts() {
        List<CategoryWithProductsVO> result = new ArrayList<>();
        String customerId = null;
        String customerGroupId = null;
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User currentUser = (User) authentication.getPrincipal();
            if ("CUSTOMER".equals(currentUser.getUserType())) {
                customerId = currentUser.getId();
                Customer customer = customerService.getById(customerId);
                if (customer != null) {
                    customerGroupId = customer.getGroupId();
                }
            }
        }
        List<ProductCategory> categories = productCategoryService.lambdaQuery().eq(ProductCategory::getIsShow, true)
                .orderByDesc(ProductCategory::getSortOrder) // 排序分类
                .list();
        for (ProductCategory category : categories) {
            CategoryWithProductsVO categoryVO = new CategoryWithProductsVO();
            BeanUtils.copyProperties(category, categoryVO); // 设置相同属性
            categoryVO.setCategoryName(category.getName()); // 设置类名
            categoryVO.setCategoryId(category.getId());     // 设置 id
            List<Product> products = productService.lambdaQuery().eq(Product::getCategoryId, category.getId()) //
                    .eq(Product::getIsPublished, true)
                    .orderByAsc(Product::getSortOrder).list();

            List<ProductDisplayVO> productDisplayVOs = new ArrayList<>();

            if (!products.isEmpty()) {
                List<String> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
                Map<String, BigDecimal> effectivePrices = priceCalculationService.calculateEffectivePrices(productIds, customerId, customerGroupId);

                for (Product product : products) {
                    ProductDisplayVO displayVO = new ProductDisplayVO();
                    BeanUtils.copyProperties(product, displayVO);
                    displayVO.setDisplayPrice(effectivePrices.get(product.getId()));
                    displayVO.setMainImage(product.getMainImage());
                    displayVO.setCategoryId(category.getId());
                    displayVO.setCategoryName(category.getName());
                    productDisplayVOs.add(displayVO);
                }
            }
            categoryVO.setChildrens(productDisplayVOs);
            result.add(categoryVO);
        }
        return result;
    }
    @GetMapping("/getOpenid/{code}")
    public String getOpenid(@PathVariable String code) {

        return wechatUtil.getOpenId(code);
    }
    @GetMapping("/product/{id}")
    public ProductDisplayVO getProduct(@PathVariable String id) {
        List<CategoryWithProductsVO> result = new ArrayList<>();
        String customerId = null;
        String customerGroupId = null;
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User currentUser = (User) authentication.getPrincipal();
            if ("CUSTOMER".equals(currentUser.getUserType())) {
                customerId = currentUser.getId();
                Customer customer = customerService.getById(customerId);
                if (customer != null) {
                    customerGroupId = customer.getGroupId();
                }
            }
        }
        return priceCalculationService.calculateEffectivePrice(id,customerId,customerGroupId);
    }
    @GetMapping("/product-search")
    public List<ProductDisplayVO> search(@RequestParam(value = "keyword") String keyword) {
        String customerId = null;
        String customerGroupId = null;
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User currentUser = (User) authentication.getPrincipal();
            if ("CUSTOMER".equals(currentUser.getUserType())) {
                customerId = currentUser.getId();
                Customer customer = customerService.getById(customerId);
                if (customer != null) {
                    customerGroupId = customer.getGroupId();
                }
            }
        }
        List<ProductDisplayVO> productDisplayVOS = new ArrayList<>();
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Product::getName, keyword);
        List<Product> products =  productService.list(queryWrapper);
        List<String> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
        Map<String, BigDecimal> effectivePrices = priceCalculationService.calculateEffectivePrices(productIds, customerId, customerGroupId);

        for (Product product : products) {
            ProductDisplayVO displayVO = new ProductDisplayVO();
            BeanUtils.copyProperties(product, displayVO);
            displayVO.setDisplayPrice(effectivePrices.get(product.getId()));
            displayVO.setMainImage(product.getMainImage());
            productDisplayVOS.add(displayVO);
        }
        return productDisplayVOS;
    }
}
