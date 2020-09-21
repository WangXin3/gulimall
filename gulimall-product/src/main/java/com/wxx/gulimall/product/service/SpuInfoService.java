package com.wxx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.product.entity.SpuInfoDescEntity;
import com.wxx.gulimall.product.entity.SpuInfoEntity;
import com.wxx.gulimall.product.vo.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO spuSaveVO);

    void saveBaseSpuInfo(SpuInfoEntity spuInfo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 上架spu
     * @param spuId /
     */
    void up(Long spuId);
}

