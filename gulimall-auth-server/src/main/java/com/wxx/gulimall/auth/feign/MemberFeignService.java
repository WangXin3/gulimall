package com.wxx.gulimall.auth.feign;

import com.wxx.common.utils.R;
import com.wxx.gulimall.auth.vo.SocialUser;
import com.wxx.gulimall.auth.vo.UserLoginVO;
import com.wxx.gulimall.auth.vo.UserRegistVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVO vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);

    @PostMapping("/member/member/oauth/login")
    R oauthLogin(@RequestBody SocialUser socialUser);
}
