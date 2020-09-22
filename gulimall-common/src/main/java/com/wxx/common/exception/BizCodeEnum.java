package com.wxx.common.exception;

/**
 * @author 她爱微笑
 * @date 2020/7/19
 */
public enum BizCodeEnum {
    /**
     * 系统未知异常
     */
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),

    /**
     * 参数格式校验失败
     */
    VALID_EXCEPTION(10001, "参数格式校验失败"),

    /**
     * 商品上架异常
     */
    PRODUCT_UP(11000, "商品上架异常")

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
