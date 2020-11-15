package com.wxx.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxx.gulimall.product.entity.AttrGroupEntity;
import com.wxx.gulimall.product.vo.SpuItemAttrGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
	
}
