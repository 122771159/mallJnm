package com.jnm.mallJnm.mapper.vo;// 文件路径: com/jnm/mallJnm/mapper/vo/OrderVOMapper.java

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jnm.mallJnm.model.vo.OrderListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface OrderVOMapper {

    // MySQL数据库强绑定
    @Select("""
            <script>
            SELECT
                o.*,
                c.name AS customerName,
                a.username AS salesName,
                (
                    SELECT JSON_ARRAYAGG(
                        JSON_OBJECT(
                            'id', oi.id, 'orderId', oi.order_id, 'productId', oi.product_id,
                            'productName', oi.product_name, 'productImage', oi.product_image,
                            'unitPrice', oi.unit_price, 'quantity', oi.quantity,
                            'lineTotalAmount', oi.line_total_amount
                        )
                    )
                    FROM jnm_order_item oi
                    WHERE oi.order_id = o.id
                ) AS orderItemsJson
            FROM
                jnm_order o
            LEFT JOIN
                jnm_customer c ON o.customer_id = c.id
            LEFT JOIN
                jnm_admin a ON o.sales_id = a.id
            <where>
                <if test="ew.userType == 'CUSTOMER'">
                    AND o.customer_id = #{ew.userId}
                </if>
                <if test="ew.userType == 'SALES' or ew.userType == 'ADMIN'">
                    <if test="ew.manageableCustomerIds != null and !ew.manageableCustomerIds.isEmpty()">
                        <if test="ew.customerId != null and ew.customerId != ''">
                             AND o.customer_id = #{ew.customerId}
                        </if>
                        <if test="ew.customerId == null or ew.customerId == ''">
                             AND o.customer_id IN
                            <foreach item='item' collection='ew.manageableCustomerIds' open='(' separator=',' close=')'>
                                #{item}
                            </foreach>
                        </if>
                    </if>
                    <if test="ew.manageableCustomerIds == null or ew.manageableCustomerIds.isEmpty()">
                        AND 1=0
                    </if>
                </if>
                <if test="ew.userType == 'SUPER' and ew.customerId != null and ew.customerId != ''">
                     AND o.customer_id = #{ew.customerId}
                </if>
                <if test="ew.status != null and ew.status != ''">
                    AND o.status = #{ew.status}
                </if>
                <if test="ew.startDate != null and ew.startDate != ''">
                    AND o.create_time &gt;= #{ew.startDate}
                </if>
                <if test="ew.endDate != null and ew.endDate != ''">
                    AND o.create_time &lt;= #{ew.endDate}
                </if>
                
                <if test="ew.keyword != null and ew.keyword != ''">
                    AND (
                        o.order_no LIKE CONCAT('%', #{ew.keyword}, '%') 
                        OR c.name LIKE CONCAT('%', #{ew.keyword}, '%')
                        
                        OR EXISTS (
                            SELECT 1 FROM jnm_order_item oi_sub
                            WHERE oi_sub.order_id = o.id
                            AND oi_sub.product_name LIKE CONCAT('%', #{ew.keyword}, '%')
                        )
                    )
                </if>
                </where>
            ORDER BY o.create_time DESC
            </script>
            """)
    Page<OrderListVO> listOrdersWithItems(Page<OrderListVO> page, @Param("ew") Map<String, Object> params);

}