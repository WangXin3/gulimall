package com.wxx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.wxx.gulimall.product.vo.SkuItemSaleAttrVO;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVO> listSaleAttrs(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

