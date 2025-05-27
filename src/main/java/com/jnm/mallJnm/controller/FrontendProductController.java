package com.jnm.mallJnm.controller;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/frontend")
public class FrontendProductController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceCalculationService priceCalculationService;
    @Autowired
    private CustomerService customerService;
    @GetMapping("/categories-with-products")
    public List<CategoryWithProductsVO> getCategoriesWithProducts() {
        List<CategoryWithProductsVO> result = new ArrayList<>();
        String customerId = null;
        String customerGroupId = null;
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User currentUser = (User) authentication.getPrincipal(); //
            if ("CUSTOMER".equals(currentUser.getUserType())) { //
                customerId = currentUser.getId(); //
                Customer customer = customerService.getById(customerId);
                if (customer != null) {
                    customerGroupId = customer.getGroupId(); //
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
                    .eq(Product::getIsPublished, true) //
                    .orderByAsc(Product::getSortOrder).list(); //

            List<ProductDisplayVO> productDisplayVOs = new ArrayList<>();

            if (!products.isEmpty()) {
                List<String> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
                Map<String, BigDecimal> effectivePrices = priceCalculationService.calculateEffectivePrices(productIds, customerId, customerGroupId);

                for (Product product : products) {
                    ProductDisplayVO displayVO = new ProductDisplayVO();
                    BeanUtils.copyProperties(product, displayVO);
                    displayVO.setDisplayPrice(effectivePrices.get(product.getId()));
//                    displayVO.setOriginalPrice(product.getPrice());
                    displayVO.setMainImage(product.getMainImage());
                    displayVO.setCategoryId(category.getId());
                    displayVO.setCategoryName(category.getName());
                    productDisplayVOs.add(displayVO);
                }
            }
            categoryVO.setProducts(productDisplayVOs);
            result.add(categoryVO);
        }
        return result;
    }
}