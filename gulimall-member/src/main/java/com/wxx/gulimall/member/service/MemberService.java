package com.wxx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.member.entity.MemberEntity;
import com.wxx.gulimall.member.exception.PhoneExistException;
import com.wxx.gulimall.member.exception.UsernameExistException;
import com.wxx.gulimall.member.vo.MemberLoginVO;
import com.wxx.gulimall.member.vo.MemberRegistVO;
import com.wxx.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:05:24
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVO vo);

    /**
     * 校验手机号是否唯一
     *
     * @param phone /
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 校验用户名是否唯一
     *
     * @param username /
     * @return
     */
    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * 用户登录
     * @param vo /
     * @return /
     */
    MemberEntity login(MemberLoginVO vo);

    /**
     * 社交账号登录和注册合并
     * @param socialUser /
     * @return /
     */
    MemberEntity login(SocialUser socialUser);
}

