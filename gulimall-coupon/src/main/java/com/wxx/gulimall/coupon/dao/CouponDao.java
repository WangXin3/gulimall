package com.wxx.gulimall.coupon.dao;

import com.wxx.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:00:55
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
