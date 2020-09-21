package com.wxx.gulimall.ware.service.impl;

import com.wxx.common.utils.R;
import com.wxx.gulimall.ware.feign.ProductFeignService;
import com.wxx.gulimall.ware.vo.SkuHasStockVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.ware.dao.WareSkuDao;
import com.wxx.gulimall.ware.entity.WareSkuEntity;
import com.wxx.gulimall.ware.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            wrapper.eq("sku_id", skuId);
        }


        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.判断如果没有库存操作 新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId)
                .eq("ware_id", wareId));

        if (wareSkuEntities == null || CollectionUtils.isEmpty(wareSkuEntities)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // 远程调用商品名称 如果失败整个事务无需回滚
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode().equals(0)) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            wareSkuDao.insert(wareSkuEntity);
        } else {
            // 2.如果有则更新
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }


    }

    @Override
    public List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(sku -> {
            SkuHasStockVO vo = new SkuHasStockVO();
            Long count = baseMapper.getSkuStock(sku);

            vo.setSkuId(sku);
            vo.setHasStock(count > 0);

            return vo;
        }).collect(Collectors.toList());


        return collect;
    }

}