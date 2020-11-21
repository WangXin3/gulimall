package com.wxx.gulimall.member.exception;

/**
 * @author wangxin
 * @date 2020/11/21
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号已存在");
    }
}
