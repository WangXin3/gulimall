package com.wxx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.product.entity.ProductAttrValueEntity;

import java.util.Map;

/**
 * spu属性值
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

