package com.wxx.gulimall.member.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;
import com.wxx.gulimall.member.dao.MemberDao;
import com.wxx.gulimall.member.dao.MemberLevelDao;
import com.wxx.gulimall.member.entity.MemberEntity;
import com.wxx.gulimall.member.entity.MemberLevelEntity;
import com.wxx.gulimall.member.exception.PhoneExistException;
import com.wxx.gulimall.member.exception.UsernameExistException;
import com.wxx.gulimall.member.service.MemberService;
import com.wxx.gulimall.member.vo.MemberLoginVO;
import com.wxx.gulimall.member.vo.MemberRegistVO;
import com.wxx.gulimall.member.vo.SocialUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVO vo) {
        MemberEntity entity = new MemberEntity();

        // 校验手机号和用户名是否唯一，如果不唯一会抛异常
        this.checkPhoneUnique(vo.getPhone());
        this.checkUsernameUnique(vo.getUsername());

        entity.setUsername(vo.getUsername());
        entity.setMobile(vo.getPhone());
        entity.setNickname("谷粒用户" + UUID.randomUUID().toString().substring(0, 5));

        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefalutLevel();
        entity.setLevelId(memberLevelEntity.getId());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(vo.getPassword()));

        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {

        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", phone);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {

        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new UsernameExistException();
        }

    }

    @Override
    public MemberEntity login(MemberLoginVO vo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", vo.getLoginAccount())
                .or().eq("mobile", vo.getLoginAccount());
        MemberEntity entity = baseMapper.selectOne(wrapper);
        if (entity == null) {
            return null;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), entity.getPassword());
        if (matches) {
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        // 登录和注册关联
        String uid = socialUser.getUid();
        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("social_uid", uid);
        MemberEntity memberEntity = baseMapper.selectOne(queryWrapper);
        if (memberEntity != null) {
            // 用户已注册，更换令牌和过期时间
            memberEntity.setSocialUid(socialUser.getUid());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());

            baseMapper.updateById(memberEntity);

            return memberEntity;
        } else {
            // 没有查到当前社交用户绑定的本网站用户，需要注册
            MemberEntity entity = new MemberEntity();
            // 查询当前社交用户的信息
            Map<String, Object> formMap = new HashMap<>(2);
            formMap.put("access_token", socialUser.getAccessToken());
            formMap.put("uid", socialUser.getUid());
            HttpResponse response = HttpRequest.get("https://api.weibo.com/2/users/show.json")
                    .form(formMap)
                    .execute();

            if (response.getStatus() == HttpStatus.HTTP_OK) {
                String body = response.body();
                Map<String, String> map = JSON.parseObject(body, new TypeReference<Map<String, String>>(){});
                entity.setNickname(map.get("name"));
                entity.setGender("M".equalsIgnoreCase(map.get("gender")) ? 1 : 0);
                entity.setHeader(map.get("profile_image_url"));
                entity.setCity(map.get("location"));

                entity.setSocialUid(socialUser.getUid());
                entity.setAccessToken(socialUser.getAccessToken());
                entity.setExpiresIn(socialUser.getExpiresIn());
            }

            MemberLevelEntity memberLevelEntity = memberLevelDao.getDefalutLevel();
            entity.setLevelId(memberLevelEntity.getId());
            baseMapper.insert(entity);
            return entity;
        }
    }

}