package com.jnm.mallJnm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.model.Order;
import com.jnm.mallJnm.model.vo.OrderCreateVO;
import com.jnm.mallJnm.model.vo.OrderListVO;
import com.jnm.mallJnm.model.vo.OrderVO;
import com.jnm.mallJnm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

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
}