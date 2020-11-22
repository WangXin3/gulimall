package com.wxx.gulimall.product.dao;

import com.wxx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxx.gulimall.product.vo.SkuItemSaleAttrVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVO> listSaleAttrs(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}
