package com.wxx.gulimall.product.dao;

import com.wxx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deletebatchrelation(@Param("data") List<AttrAttrgroupRelationEntity> data);
}
