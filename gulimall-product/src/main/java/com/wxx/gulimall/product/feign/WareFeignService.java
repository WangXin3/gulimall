package com.wxx.gulimall.product.feign;

import com.wxx.common.dto.SkuHasStockVO;
import com.wxx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/9/21
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R<List<SkuHasStockVO>> getSkusHasStock(@RequestBody List<Long> skuIds);
}
