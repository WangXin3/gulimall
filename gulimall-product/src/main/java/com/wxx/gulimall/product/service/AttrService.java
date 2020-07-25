package com.wxx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.product.entity.AttrEntity;
import com.wxx.gulimall.product.vo.AttrEntityResp;
import com.wxx.gulimall.product.vo.AttrEntityVO;
import com.wxx.gulimall.product.vo.AttrGroupRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 20:58:47
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrEntityVO attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrEntityResp getAttrInfo(Long attrId);

    void updateAttr(AttrEntityVO attr);

    /**
     * 根据分组id查找关联的所有基本属性
     * @return
     * @param attrgroupId
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVO[] vos);

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

