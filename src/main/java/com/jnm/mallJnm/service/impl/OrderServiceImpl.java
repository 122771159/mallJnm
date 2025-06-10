package com.jnm.mallJnm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.exception.ValidatedException;
import com.jnm.mallJnm.mapper.OrderMapper;
import com.jnm.mallJnm.mapper.vo.OrderVOMapper;
import com.jnm.mallJnm.model.*;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.model.enums.OrderStatus;
import com.jnm.mallJnm.model.enums.UserType;
import com.jnm.mallJnm.model.vo.OrderCreateVO;
import com.jnm.mallJnm.model.vo.OrderListVO;
import com.jnm.mallJnm.model.vo.OrderVO;
import com.jnm.mallJnm.model.vo.User;
import com.jnm.mallJnm.security.utils.SecurityUtils;
import com.jnm.mallJnm.service.*;
import com.jnm.mallJnm.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderVOMapper orderVOMapper; // 需要一个新的Mapper来处理连表查询

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateVO orderCreateVO) {
        // 1. 参数校验
        if (orderCreateVO == null || CollectionUtils.isEmpty(orderCreateVO.getItems())) {
            throw new ValidatedException(ErrorEnum.OBJECT_EMPTY.getCode(), "订单商品不能为空");
        }

        // 2. 获取客户和地址信息
        Customer customer = customerService.getById(orderCreateVO.getCustomerId());
        if (customer == null) {
            throw new ValidatedException(ErrorEnum.NOT_EXIST_ERROR.getCode(), "客户不存在");
        }
        // 2.1 判断操作人信息是否正确
        User user = SecurityUtils.getCurrentUser();
        if (!user.getUserType().equals(UserType.CUSTOMER.name())) {
            if (!customer.getAid().equals(user.getId())) {
                throw new ServerException(ErrorEnum.IDENTITY_ERROR);
            }
        } else {
            if (!customer.getId().equals(user.getId())) {
                throw new ServerException(ErrorEnum.IDENTITY_ERROR);
            }
        }
        // 2.2 判断地址信息是否属于此客户
        Address address = addressService.getById(orderCreateVO.getAddressId());
        if (address == null || !address.getCid().equals(customer.getId())) {
            throw new ValidatedException(ErrorEnum.NOT_EXIST_ERROR.getCode(), "地址信息有误");
        }

        // 3. 批量获取商品信息和计算价格 (核心)
        List<String> productIds = orderCreateVO.getItems().stream()
                .map(OrderCreateVO.CartItemVO::getProductId).toList();

        // 3.1 批量获取商品最新信息
        Map<String, Product> productMap = productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 3.2 批量计算所有商品的最终成交价
        Map<String, BigDecimal> effectivePrices = priceCalculationService.calculateEffectivePrices(
                productIds, customer.getId(), customer.getGroupId());

        // 4. 组装订单项 (OrderItem) 并计算商品总金额
        List<OrderItem> orderItemsToSave = new ArrayList<>();
        BigDecimal totalProductAmount = BigDecimal.ZERO;

        for (OrderCreateVO.CartItemVO item : orderCreateVO.getItems()) {
            Product product = productMap.get(item.getProductId());
            BigDecimal unitPrice = effectivePrices.get(item.getProductId());

            if (product == null || unitPrice == null) {
                throw new ValidatedException(ErrorEnum.NOT_EXIST_ERROR.getCode(), "商品ID: " + item.getProductId() + " 信息有误");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new ValidatedException(220, "商品 \"" + product.getName() + "\" 库存不足");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName()); // 快照
            orderItem.setProductImage(product.getMainImage()); // 快照
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(unitPrice); // 快照最终成交价

            BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(item.getQuantity()));
            orderItem.setLineTotalAmount(lineTotal);

            orderItemsToSave.add(orderItem);
            totalProductAmount = totalProductAmount.add(lineTotal);
        }

        // 5. 组装订单主表 (Order)
        Order order = new Order();
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setOrderNo(StringUtil.createNo());
        order.setCustomerId(customer.getId());
        order.setSalesId(customer.getAid()); // 快照业务员ID

        // 地址信息快照
        order.setShippingName(address.getName());
        order.setShippingPhone(address.getPhone());
        order.setShippingAddress(address.getAddress());

        order.setCustomerRemark(orderCreateVO.getCustomerRemark());
        order.setProductAmount(totalProductAmount);
        order.setShippingFee(BigDecimal.ZERO); // B2B业务，运费可后续由管理员修改，当前为0
        order.setTotalAmount(totalProductAmount.add(order.getShippingFee()));
        order.setStatus(OrderStatus.COMPLETED.name());

        // 6. 保存订单和订单项到数据库
        this.save(order); // 保存主订单，MyBatis-Plus会自动回填ID

        // 关联订单ID到所有订单项
        for (OrderItem orderItem : orderItemsToSave) {
            orderItem.setOrderId(order.getId());
        }
        orderItemService.saveBatch(orderItemsToSave); // 批量插入订单项
        for (OrderItem orderItem : orderItemsToSave) {
            boolean success = productService.deductStock(orderItem.getProductId(), orderItem.getQuantity());
            if (!success) {
                // 如果扣减失败，立即抛出异常，整个事务将回滚
                Product product = productMap.get(orderItem.getProductId());
                throw new ValidatedException(221, "商品 \"" + product.getName() + "\" 库存不足，下单失败");
            }
        }
        // 7. 组装并返回结果
        OrderVO resultVO = new OrderVO();
        BeanUtils.copyProperties(order, resultVO);
        resultVO.setOrderItems(orderItemsToSave);
        return resultVO;
    }

    @Override
    public OrderVO getOrderDetails(String orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            return null;
        }
        List<OrderItem> items = orderItemService.lambdaQuery().eq(OrderItem::getOrderId, orderId).list();

        OrderVO resultVO = new OrderVO();
        BeanUtils.copyProperties(order, resultVO);
        resultVO.setOrderItems(items);
        return resultVO;
    }
    // 完整替换 com/jnm/mallJnm/service/impl/OrderServiceImpl.java 中的 listOrders 方法

    //    @Override
//    public Page<OrderListVO> listOrders(int pageNum, int pageSize, String status, String customerId, String keyword, String startDate, String endDate) {
//        User currentUser = SecurityUtils.getCurrentUser();
//
//        JoinWrapper<OrderListVO> wrapper = new JoinWrapper<>();
//
//        // 1. 优化SELECT子句：使用GROUP_CONCAT聚合商品信息
//        wrapper.alias("o")
//                .select("o.*, c.name as customerName, a.username as salesName, " +
//                        "GROUP_CONCAT(oi.product_name SEPARATOR '||') as productSummary") // [优化一]
//                .leftJoin("jnm_customer c ON o.customer_id = c.id")
//                .leftJoin("jnm_admin a ON o.sales_id = a.id")
//                .leftJoin("jnm_order_item oi ON o.id = oi.order_id"); // JOIN订单项表
//
//        // 2. 权限规则 (与之前保持一致)
//        switch (UserType.valueOf(currentUser.getUserType())) {
//            case CUSTOMER:
//                wrapper.eq("o.customer_id", currentUser.getId());
//                break;
//            case SALES:
//            case ADMIN:
//                List<String> manageableCustomerIds = customerService.lambdaQuery()
//                        .eq(Customer::getAid, currentUser.getId())
//                        .select(Customer::getId)
//                        .list().stream().map(Customer::getId).toList();
//
//                if (manageableCustomerIds.isEmpty()) {
//                    return new Page<>(pageNum, pageSize);
//                }
//
//                if (!StringUtil.isNullOrEmpty(customerId)) {
//                    if (!manageableCustomerIds.contains(customerId)) {
//                        throw new ValidatedException(ErrorEnum.NO_AUTHORITY);
//                    }
//                    wrapper.eq("o.customer_id", customerId);
//                } else {
//                    wrapper.in("o.customer_id", manageableCustomerIds);
//                }
//                break;
//            case SUPER:
//                if (!StringUtil.isNullOrEmpty(customerId)) {
//                    wrapper.eq("o.customer_id", customerId);
//                }
//                break;
//        }
//
//        // 3. 应用筛选条件
//        // 状态筛选
//        wrapper.eq(!StringUtil.isNullOrEmpty(status), "o.status", status);
//
//        // [优化二] 关键词筛选：扩展到商品名称
//        if (!StringUtil.isNullOrEmpty(keyword)) {
//            wrapper.and(w -> w.like("o.order_no", keyword)
//                    .or().like("c.name", keyword)
//                    .or().like("oi.product_name", keyword)); // 搜索商品名
//        }
//
//        // [新功能] 时间范围筛选
//        if (!StringUtil.isNullOrEmpty(startDate)) {
//            wrapper.ge("o.create_time", startDate + " 00:00:00");
//        }
//        if (!StringUtil.isNullOrEmpty(endDate)) {
//            wrapper.le("o.create_time", endDate + " 23:59:59");
//        }
//
//        // 4. 分组和排序
//        wrapper.groupBy("o.id"); // 必须按订单ID分组，防止因JOIN导致订单列表重复
//        wrapper.orderByDesc("o.create_time");
//
//        Page<OrderListVO> page = new Page<>(pageNum, pageSize);
//        return orderVOMapper.selectJoinPage(page, wrapper);
//    }
    @Override
    public Page<OrderListVO> listOrders(int pageNum, int pageSize, String status, String customerId, String keyword, String startDate, String endDate) {
        User currentUser = SecurityUtils.getCurrentUser();

        // 构造参数Map传递给Mapper
        Map<String, Object> params = new HashMap<>();
        params.put("userType", currentUser.getUserType());
        params.put("userId", currentUser.getId());
        // 权限处理
        if (UserType.valueOf(currentUser.getUserType()) == UserType.SALES || UserType.valueOf(currentUser.getUserType()) == UserType.ADMIN) {
            List<String> manageableCustomerIds = customerService.lambdaQuery()
                    .eq(Customer::getAid, currentUser.getId())
                    .select(Customer::getId)
                    .list().stream().map(Customer::getId).toList();
            params.put("manageableCustomerIds", manageableCustomerIds);
        }

        // 筛选条件
        params.put("status", status);
        params.put("customerId", customerId);
        params.put("keyword", keyword);
        if (!StringUtil.isNullOrEmpty(startDate)) params.put("startDate", startDate + " 00:00:00");
        if (!StringUtil.isNullOrEmpty(endDate)) params.put("endDate", endDate + " 23:59:59");

        Page<OrderListVO> page = new Page<>(pageNum, pageSize);
        return orderVOMapper.listOrdersWithItems(page, params);
    }

    @Override
    public void cancelOrder(Order order) {
        User currentUser = SecurityUtils.getCurrentUser();
        String userType = currentUser.getUserType();
        if (userType.equals(UserType.CUSTOMER.name())) {
            throw new ValidatedException(ErrorEnum.NO_AUTHORITY);
        }
        if (userType.equals(UserType.SALES.name())) {
            Customer byId = customerService.getById(order.getCustomerId());
            if (byId == null) {
                throw new ValidatedException(ErrorEnum.CID_EMPTY);
            }
            if (!byId.getAid().equals(order.getCustomerId())) {
                throw new ValidatedException(ErrorEnum.NO_AUTHORITY);
            }

        }
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Order::getStatus, OrderStatus.CANCELLED.name());
        updateWrapper.set(Order::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(Order::getCustomerId, order.getCustomerId());
        updateWrapper.eq(Order::getId, order.getId());
        updateWrapper.eq(Order::getOrderNo, order.getOrderNo());
        this.update(updateWrapper);
    }
}