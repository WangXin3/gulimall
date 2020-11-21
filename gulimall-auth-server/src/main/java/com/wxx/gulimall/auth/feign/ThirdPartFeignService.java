package com.wxx.gulimall.auth.feign;

import com.wxx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {

    /**
     * 发送短信
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
