package com.wxx.gulimall.coupon.service.impl;

import com.wxx.common.dto.MemberPrice;
import com.wxx.common.dto.SkuReductionDTO;
import com.wxx.gulimall.coupon.entity.MemberPriceEntity;
import com.wxx.gulimall.coupon.entity.SkuLadderEntity;
import com.wxx.gulimall.coupon.service.MemberPriceService;
import com.wxx.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.coupon.dao.SkuFullReductionDao;
import com.wxx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.wxx.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionDTO skuReductionDTO) {
        // 1.保存满减打折，会员价
        // 6.4 保存sku的优惠满减信息 sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionDTO.getSkuId());
        skuLadderEntity.setFullCount(skuReductionDTO.getFullCount());
        skuLadderEntity.setDiscount(skuReductionDTO.getDiscount());
        skuLadderEntity.setAddOther(skuReductionDTO.getCountStatus());
        if (skuReductionDTO.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }


        // sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionDTO, skuFullReductionEntity);
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionDTO.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream()
                .map(item -> {
                    MemberPriceEntity entity = new MemberPriceEntity();
                    entity.setSkuId(item.getId());
                    entity.setMemberLevelName(item.getName());
                    entity.setMemberPrice(item.getPrice());
                    entity.setAddOther(1);

                    return entity;
                })
                .filter(item -> item.getMemberPrice().compareTo(new BigDecimal("0")) == 1)
                .collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}