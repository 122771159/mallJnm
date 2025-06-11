package com.jnm.mallJnm.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.jnm.mallJnm.model.OrderItem;
import com.jnm.mallJnm.model.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ColumnWidth(20) // 统一设置列宽
public class OrderExportVO {

    @ExcelProperty("订单号")
    private String orderNo;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("所属业务员")
    private String salesName;
    @ExcelProperty("订单状态")
    private String status;
    @ExcelProperty("商品总额")
    private BigDecimal productAmount;
    @ExcelProperty("运费")
    private BigDecimal shippingFee;
    @ExcelProperty("订单总额")
    private BigDecimal totalAmount;
    @ExcelProperty("收货人")
    private String shippingName;
    @ExcelProperty("联系电话")
    private String shippingPhone;
    @ExcelProperty(value = "收货地址")
    @ColumnWidth(40)
    private String shippingAddress;
    @ExcelProperty(value = "客户备注")
    @ColumnWidth(40)
    private String customerRemark;
    @ExcelProperty(value = "商品详情")
    @ColumnWidth(50)
    private String orderItemsSummary;
    @ExcelProperty(value = "下单时间")
    private LocalDateTime createTime;
    @ExcelProperty(value = "作废原因")
    private String reason;
    /**
     * 自定义转换逻辑，将订单项列表转换为易于阅读的字符串。
     * @param orderItems 订单项列表
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            this.orderItemsSummary = "";
            return;
        }
        this.orderItemsSummary = orderItems.stream()
                .map(item -> String.format("%s (单价: %.2f, 数量: %d)",
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity()))
                .collect(Collectors.joining("; "));
    }
    public void setStatus(String status) {
        if(status.equals(OrderStatus.COMPLETED.name())){
            this.status = OrderStatus.COMPLETED.toString();
        }
        else if(status.equals(OrderStatus.CANCELLED.name())){
            this.status = OrderStatus.CANCELLED.toString();
        }
    }
}