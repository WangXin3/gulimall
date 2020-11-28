package com.wxx.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangxin
 * @date 2020/11/28
 */
@Data
public class OrderConfirmVO {

    /**
     * 收货地址列表
     */
    private List<MemberAddressVO> address;

    /**
     * 所有选中的购物项
     */
    private List<OrderItemVO> items;

    /**
     * 积分
     */
    private Integer integration;

    /**
     * 订单总额
     */
    private BigDecimal total;

    /**
     * 应付总额
     */
    private BigDecimal payPrice;


}
