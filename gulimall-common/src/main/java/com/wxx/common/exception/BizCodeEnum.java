package com.wxx.common.exception;

/**
 * @author 她爱微笑
 * @date 2020/7/19
 * 错误码列表
 * 前缀两个数字代表不同微服务
 * 10: 通用
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 * 15：用户
 */
public enum BizCodeEnum {

    UNKNOWN_EXCEPTION(10000, "系统未知异常"),

    VALID_EXCEPTION(10001, "参数格式校验失败"),

    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后重试"),

    PRODUCT_UP(11000, "商品上架异常"),

    USERNAME_EXIST_EXCEPTION(15001, "用户名存在异常"),

    PHONE_EXIST_EXCEPTION(15002, "手机号存在异常"),

    LOGIN_ACCOUNT_PASSWORD_INVALID_EXCEPTION(15003, "账号或密码错误"),
    ;

    /**
     * 状态码
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
