package com.wxx.gulimall.order.dao;

import com.wxx.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:13:29
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
