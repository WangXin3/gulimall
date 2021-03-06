package com.wxx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.ware.entity.WareSkuEntity;
import com.wxx.gulimall.ware.vo.SkuHasStockVO;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:16:46
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds);
}

