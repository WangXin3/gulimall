package com.wxx.gulimall.product.feign;

import com.wxx.common.dto.SkuReductionDTO;
import com.wxx.common.dto.SpuBoundDTO;
import com.wxx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 她爱微笑
 * @date 2020/7/26
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {


    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundDTO spuBoundDTO);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionDTO skuReductionDTO);
}
