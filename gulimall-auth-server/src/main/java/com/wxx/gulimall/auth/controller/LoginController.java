package com.wxx.gulimall.auth.controller;

import com.wxx.common.constant.AuthServerConstant;
import com.wxx.common.exception.BizCodeEnum;
import com.wxx.common.utils.R;
import com.wxx.gulimall.auth.feign.MemberFeignService;
import com.wxx.gulimall.auth.feign.ThirdPartFeignService;
import com.wxx.gulimall.auth.vo.UserLoginVO;
import com.wxx.gulimall.auth.vo.UserRegistVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        // 1.接口防刷 redis

        // 2.验证码校验
        String value = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX.concat(phone));
        if (StringUtils.isNotBlank(value)) {
            long l = Long.parseLong(value.split("_")[1]);
            if (System.currentTimeMillis() - l < 60 * 1000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX.concat(phone);
        String code = UUID.randomUUID().toString().substring(0, 5).concat("_" + System.currentTimeMillis());

        // 验证码在redis缓存5分钟
        stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

//        thirdPartFeignService.sendCode(phone, code.split("_")[0]);
        return R.ok();
    }

    /**
     * 注册
     * @param userRegistVO vo
     * @param bindingResult 参数校验结果
     * @param redirectAttributes 模拟重定向携带数据
     * @return /
     */
    @PostMapping("/regist")
    @SuppressWarnings("all")
    public String regist(@Valid UserRegistVO userRegistVO, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        // 校验出错
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));


            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        // 真正注册
        // 校验验证码
        String code = userRegistVO.getCode();

        String value = stringRedisTemplate.opsForValue()
                .get(AuthServerConstant.SMS_CODE_CACHE_PREFIX.concat(userRegistVO.getPhone()));
        Map<String, String> errors = new HashMap<>();
        if (StringUtils.isBlank(value) || !value.split("_")[0].equals(code)) {

            errors.put("code", "验证码错误");

            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        // 验证通过
        // 先删除验证码
        stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX.concat(userRegistVO.getPhone()));
        // 注册
        R r = memberFeignService.regist(userRegistVO);
        if (r.getCode() == 0) {
            // 注册成功
            return "redirect:http://auth.gulimall.com/login.html";
        } else {
            // 注册失败
            errors.put("msg", r.get("msg").toString());
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVO vo, RedirectAttributes redirectAttributes) {
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            // 登录成功
            return "redirect:http://gulimall.com";
        } else {
            // 登录失败
            Map<String, String> errors = new HashMap<>(1);
            errors.put("msg", r.get("msg").toString());
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
