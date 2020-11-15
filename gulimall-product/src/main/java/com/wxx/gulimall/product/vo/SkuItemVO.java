package com.wxx.gulimall.product.vo;

import com.wxx.gulimall.product.entity.SkuImagesEntity;
import com.wxx.gulimall.product.entity.SkuInfoEntity;
import com.wxx.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wangxin
 * @date 2020/11/15
 */
@Data
public class SkuItemVO {

    private SkuInfoEntity skuInfoEntity;

    private Boolean hasStock = true;

    private List<SkuImagesEntity> images;

    /**
     * 获取spu的销售属性组合
     */
    private List<SkuItemSaleAttrVO> saleAttr;

    /**
     * 获取spu的介绍
     */
    private SpuInfoDescEntity desc;

    /**
     * 获取spu的规格参数信息
     */
    private List<SpuItemAttrGroupVO> groupAttrs;

    /**
     * 秒杀商品的优惠信息
     */
    private SeckillSkuVO seckillSkuVO;
}
