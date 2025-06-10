package com.jnm.mallJnm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jnm.mallJnm.model.Order;
import com.jnm.mallJnm.model.vo.OrderCreateVO;
import com.jnm.mallJnm.model.vo.OrderListVO;
import com.jnm.mallJnm.model.vo.OrderVO;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     * @param orderCreateVO 包含创建订单所需信息的VO对象
     * @return 创建成功的订单信息
     */
    OrderVO createOrder(OrderCreateVO orderCreateVO);

    /**
     * 根据ID获取订单详情（包含订单项）
     * @param orderId 订单ID
     * @return 订单详情VO
     */
    OrderVO getOrderDetails(String orderId);

    // 后续可以继续添加其他业务方法，例如：
    // void approveOrder(String orderId); // 审批订单
    // void shipOrder(String orderId, String trackingNumber); // 发货

    /**
     * 根据不同角色和条件分页查询订单列表
     */
    Page<OrderListVO> listOrders(int pageNum, int pageSize, String status, String customerId, String keyword, String startDate, String endDate);

    /**
     * 作废订单
     */
    void cancelOrder(Order order);
}