package com.wxx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxx.common.utils.PageUtils;
import com.wxx.gulimall.ware.entity.PurchaseEntity;
import com.wxx.gulimall.ware.vo.MergeVO;
import com.wxx.gulimall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:16:46
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageunReceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVO);

    /**
     * 领取采购单
     * @param ids 采购单id
     */
    void received(List<Long> ids);

    void done(PurchaseDoneVO vo);
}


