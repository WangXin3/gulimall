package com.wxx.gulimall.product.dao;

import com.wxx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCagegory(@Param("catId") Long catId,@Param("name") String name);
}
