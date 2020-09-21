package com.wxx.gulimall.product.service.impl;

import com.wxx.common.dto.SkuHasStockVO;
import com.wxx.common.dto.SkuReductionDTO;
import com.wxx.common.dto.SpuBoundDTO;
import com.wxx.common.dto.es.SkuEsModel;
import com.wxx.common.utils.R;
import com.wxx.gulimall.product.entity.*;
import com.wxx.gulimall.product.feign.CouponFeignService;
import com.wxx.gulimall.product.feign.WareFeignService;
import com.wxx.gulimall.product.service.*;
import com.wxx.gulimall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService saveSpuInfoDecript;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * TODO 分布式事务seata
     *
     * @param spuSaveVO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVO spuSaveVO) {
        // 1.保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVO, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfo);

        // 2.保存spu的描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVO.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfo.getId());
        descEntity.setDecript(String.join(",", decript));
        saveSpuInfoDecript.saveSpuInfoDecript(descEntity);

        // 3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVO.getImages();
        spuImagesService.saveImages(spuInfo.getId(), images);

        // 4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVO.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrs = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(attr.getAttrId());

            AttrEntity byId = attrService.getById(attr.getAttrId());
            entity.setAttrName(byId.getAttrName());
            entity.setAttrValue(attr.getAttrValues());
            entity.setQuickShow(attr.getShowDesc());
            entity.setSpuId(spuInfo.getId());

            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(productAttrs);

        // 5.保存spu的积分信息 gulimall_sms sms_spu_bounds
        Bounds bounds = spuSaveVO.getBounds();
        SpuBoundDTO spuBoundDTO = new SpuBoundDTO();
        BeanUtils.copyProperties(bounds, spuBoundDTO);
        spuBoundDTO.setSpuId(spuInfo.getId());

        R r = couponFeignService.saveSpuBounds(spuBoundDTO);
        if (!r.getCode().equals(0)) {
            log.error("远程保存spu积分信息失败");
        }

        // 6.保存当前spu对应的所有sku信息

        List<Skus> skus = spuSaveVO.getSkus();

        if (skus != null && !CollectionUtils.isEmpty(skus)) {
            skus.forEach(sku -> {
//                private String skuName;
//                private BigDecimal price;
//                private String skuTitle;
//                private String skuSubtitle;
                String defaultImg = null;

                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg().equals(1)) {
                        defaultImg = image.getImgUrl();
                    }
                }

                // 6.1 保存sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfo.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 6.2 保存sku描述图片 pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntityList = sku.getImages().stream()
                        .filter(img -> !StringUtils.isBlank(img.getImgUrl()))
                        .map(img -> {
                            SkuImagesEntity entity = new SkuImagesEntity();
                            entity.setSkuId(skuId);
                            entity.setImgUrl(img.getImgUrl());
                            entity.setDefaultImg(img.getDefaultImg());

                            return entity;
                        }).collect(Collectors.toList());
                //TODO 没有图片路径的无需保存
                skuImagesService.saveBatch(imagesEntityList);


                // 6.3 保存sku的销售属性 pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuAttrList = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);

                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuAttrList);

                // 6.4 保存sku的优惠满减信息 gulimall_sms sms_sku_ladder/sms_sku_full_reduction/sms_member_price
                SkuReductionDTO skuReductionDTO = new SkuReductionDTO();
                BeanUtils.copyProperties(sku, skuReductionDTO);
                skuReductionDTO.setSkuId(skuId);
                if (skuReductionDTO.getFullCount() > 0 || skuReductionDTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionDTO);
                    if (!r1.getCode().equals(0)) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfo) {
        this.baseMapper.insert(spuInfo);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            wrapper.and(w -> w.eq("id", key).or().like("spu_name", key));
        }

        String status = (String) params.get("order");
        if (!StringUtils.isBlank(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }


        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        // 1. 查询当前spuid对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODO 4. 查询当前sku的所有可以用来被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

        List<Long> searchAttrId = attrService.selectSearchAttrIds(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrId);

        List<SkuEsModel.Attrs> collect = baseAttrs.stream()
                .filter(b -> idSet.contains(b.getAttrId()))
                .map(b -> {
                    SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(b, attr);

                    return attr;
                }).collect(Collectors.toList());


        Map<Long, Boolean> skuHasStockMap = null;
        try {
            List<SkuHasStockVO> skuHasStockVOS = wareFeignService.getSkusHasStock(skuIds).getData();

            skuHasStockMap = skuHasStockVOS.stream()
                    .collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
        } catch (Exception e) {
            log.error("日志服务查询异常:原因{}", e);
        }


        // 2. 封装每个sku的信息
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // TODO 1. 发送远程调用, 查询库存系统是否有库存
            if (finalSkuHasStockMap != null) {
                esModel.setHasStock(finalSkuHasStockMap.get(sku.getSkuId()));
            } else {
                esModel.setHasStock(true);
            }

            // TODO 2. 热度评分, 默认0
            esModel.setHotScore(0L);

            // TODO 3. 查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatelogId());
            esModel.setCatelogName(category.getName());


            esModel.setAttrs(collect);

            return esModel;
        }).collect(Collectors.toList());


        // TODO 5. 将数据发送给es进行保存


    }


}