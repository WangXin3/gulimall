package com.wxx.gulimall.member.exception;

/**
 * @author wangxin
 * @date 2020/11/21
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名已存在");
    }
}
