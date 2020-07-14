package com.wxx.gulimall.member.feign;

import com.wxx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 她爱微笑
 * @date 2020/7/13
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 查询优惠卷
     * @return /
     */
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
