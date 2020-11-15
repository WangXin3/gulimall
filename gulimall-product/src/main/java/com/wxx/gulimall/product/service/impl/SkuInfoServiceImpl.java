package com.wxx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;
import com.wxx.gulimall.product.dao.SkuInfoDao;
import com.wxx.gulimall.product.entity.SkuImagesEntity;
import com.wxx.gulimall.product.entity.SkuInfoEntity;
import com.wxx.gulimall.product.entity.SpuInfoDescEntity;
import com.wxx.gulimall.product.service.*;
import com.wxx.gulimall.product.vo.SkuItemSaleAttrVO;
import com.wxx.gulimall.product.vo.SkuItemVO;
import com.wxx.gulimall.product.vo.SpuItemAttrGroupVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 225
         * brandId: 1
         * min: 0
         * max: 0
         */

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("sku_id", key).or().like("sku_name", key));
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNotBlank(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (StringUtils.isNotBlank(max)) {
            BigDecimal decimal = new BigDecimal(max);
            if (decimal.compareTo(new BigDecimal("0")) == 1) {
                wrapper.le("price", max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO vo = new SkuItemVO();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.sku基本信息获取 pms_sku_info
            SkuInfoEntity byId = getById(skuId);
            vo.setSkuInfoEntity(byId);
            return byId;
        }, executor);

        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((res) -> {
            // 3.获取spu销售属性组合
            List<SkuItemSaleAttrVO> saleAttrVos = skuSaleAttrValueService.listSaleAttrs(res.getSpuId());
            vo.setSaleAttr(saleAttrVos);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            // 4.获取spu的介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
            vo.setDesc(spuInfo);
        }, executor);

        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 5.获取spu规格参数信息
            List<SpuItemAttrGroupVO> attrGroupsVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            vo.setGroupAttrs(attrGroupsVos);
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2.sku图片信息 pms_sku_images
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            vo.setImages(images);
        }, executor);

        // 等待所有任务都完成 .join会抛出未经检查的异常，不会强制开发者处理异常 .get会抛出检查异常，需要开发者处理
        CompletableFuture.allOf(saleFuture, descFuture, baseAttrFuture, imagesFuture).join();

        return vo;
    }

}