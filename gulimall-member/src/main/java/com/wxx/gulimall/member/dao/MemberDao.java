package com.wxx.gulimall.member.dao;

import com.wxx.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:05:24
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
