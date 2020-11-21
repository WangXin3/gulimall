package com.wxx.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@Data
public class UserRegistVO {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位")
    private String username;

    @NotEmpty(message = "密码必须提交")
    @Length(min = 6, max = 16, message = "密码长度必须为6-18位")
    private String password;

    @NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码必须填写")
    private String code;
}
