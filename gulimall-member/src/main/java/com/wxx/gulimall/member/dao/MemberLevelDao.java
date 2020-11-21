package com.wxx.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxx.gulimall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:05:24
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefalutLevel();

}
