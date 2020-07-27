package com.wxx.gulimall.ware.service.impl;

import com.wxx.common.constant.WareConstant;
import com.wxx.gulimall.ware.entity.PurchaseDetailEntity;
import com.wxx.gulimall.ware.service.PurchaseDetailService;
import com.wxx.gulimall.ware.service.WareSkuService;
import com.wxx.gulimall.ware.vo.MergeVO;
import com.wxx.gulimall.ware.vo.PurchaseDoneVO;
import com.wxx.gulimall.ware.vo.PurchaseItemDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.ware.dao.PurchaseDao;
import com.wxx.gulimall.ware.entity.PurchaseEntity;
import com.wxx.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageunReceivePurchase(Map<String, Object> params) {

        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVO mergeVO) {
        Long purchaseId = mergeVO.getPurchaseId();
        // 新建
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        PurchaseEntity byId = this.getById(purchaseId);
        // 合并整单 采购单必须是新建或者已分配状态
        if (byId.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode()
                || byId.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()) {

            List<Long> items = mergeVO.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(i);
                entity.setPurchaseId(finalPurchaseId);
                entity.setStatus(WareConstant.PurchaseDetailStatus.ASSIGNED.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    @Override
    public void received(List<Long> ids) {
        // 1. 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter(item ->
                item.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()
        ).map(item -> {
            // 2. 改变采购单的状态
            item.setStatus(WareConstant.PurchaseStatus.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);

        // 3. 改变采购项的状态
        collect.forEach(item -> {
            List<PurchaseDetailEntity> detailList = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = detailList.stream().map(entity -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(entity.getId());
                detailEntity.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseDoneVO vo) {


        // 1.改变采购项状态
        Boolean flag = true;
        List<PurchaseItemDoneVO> items = vo.getItems();

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVO item : items) {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();

            // 有一个采购项失败，整个采购单状态为 有异常
            if (item.getStatus() == WareConstant.PurchaseDetailStatus.HASERROR.getCode()) {
                flag = false;
            } else {
                // 3.将成功采购的进行入库
                // 根据采购项查询skuid
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());


                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }


            entity.setId(item.getItemId());
            entity.setStatus(item.getStatus());
            updates.add(entity);
        }
        purchaseDetailService.updateBatchById(updates);

        // 2.改变采购单状态
        @NotNull Long id = vo.getId();
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(id);
        entity.setStatus(flag ? WareConstant.PurchaseStatus.FINISH.getCode() : WareConstant.PurchaseStatus.HASERROR.getCode());
        entity.setUpdateTime(new Date());
        this.updateById(entity);




    }

}