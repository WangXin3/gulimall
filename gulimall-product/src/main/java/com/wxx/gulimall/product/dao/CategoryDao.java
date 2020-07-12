package com.wxx.gulimall.product.dao;

import com.wxx.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
