package com.wxx.gulimall.member.vo;

import lombok.Data;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private Long expiresIn;

    private String uid;

    private String isRealName;
}
