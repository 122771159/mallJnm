package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.config.NoWrapper;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.Customer;
import com.jnm.mallJnm.model.Order;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.OrderCreateVO;
import com.jnm.mallJnm.model.vo.OrderListVO;
import com.jnm.mallJnm.model.vo.OrderVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.CustomerService;
import com.jnm.mallJnm.service.OrderService;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    /**
     * 创建订单
     */
    @PostMapping
    public OrderVO create(@RequestBody OrderCreateVO orderCreateVO) {


        return orderService.createOrder(orderCreateVO);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public OrderVO getDetails(@PathVariable String id) {
        return orderService.getOrderDetails(id);
    }

    /**
     * 分页查询订单列表 (支持多角色和筛选)
     */
    @GetMapping
    public Page<OrderListVO> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate, // 新增：开始日期
            @RequestParam(required = false) String endDate) { // 新增：结束日期
        return orderService.listOrders(pageNum, pageSize, status, customerId, keyword, startDate, endDate);
    }
    @PostMapping("/cancel")
    public void cancel(@RequestBody Order order) {
        orderService.cancelOrder(order);
    }

    @GetMapping("/export")
    @NoWrapper
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 复用分页查询的权限和参数构建逻辑
        User currentUser = SecurityUtils.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userType", currentUser.getUserType());
        params.put("userId", currentUser.getId());
        //  || UserType.valueOf(currentUser.getUserType()) == UserType.ADMIN
        if (UserType.valueOf(currentUser.getUserType()) == UserType.SALES) {
            List<String> manageableCustomerIds = customerService.lambdaQuery()
                    .eq(Customer::getAid, currentUser.getId())
                    .select(Customer::getId)
                    .list().stream().map(Customer::getId).toList();
            params.put("manageableCustomerIds", manageableCustomerIds);
        }
        // 绑定筛选参数
        params.put("status", status);
        params.put("customerId", customerId);
        params.put("keyword", keyword);
        if (!StringUtil.isNullOrEmpty(startDate)) params.put("startDate", startDate + " 00:00:00");
        if (!StringUtil.isNullOrEmpty(endDate)) params.put("endDate", endDate + " 23:59:59");
        // 2. 调用Service获取Excel文件的字节数组
        byte[] excelBytes = orderService.exportOrders(params);

        // 3. 构建HTTP响应头
        HttpHeaders headers = new HttpHeaders();
        try {
            String fileName = URLEncoder.encode("订单导出记录.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment", fileName);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 4. 返回ResponseEntity对象，封装了字节数据、响应头和HTTP状态码
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}