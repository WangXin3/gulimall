package com.wxx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxx.common.constant.ProductConstant;
import com.wxx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wxx.gulimall.product.dao.AttrGroupDao;
import com.wxx.gulimall.product.dao.CategoryDao;
import com.wxx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wxx.gulimall.product.entity.AttrGroupEntity;
import com.wxx.gulimall.product.entity.CategoryEntity;
import com.wxx.gulimall.product.service.CategoryService;
import com.wxx.gulimall.product.vo.AttrEntityResp;
import com.wxx.gulimall.product.vo.AttrEntityVO;
import com.wxx.gulimall.product.vo.AttrGroupRelationVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.product.dao.AttrDao;
import com.wxx.gulimall.product.entity.AttrEntity;
import com.wxx.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrEntityVO attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);

        this.save(entity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            // 保存关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(entity.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> w.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);
        // 取出封装好的数据
        List<AttrEntity> records = page.getRecords();


        List<AttrEntityResp> resps = records.stream().map(attrEntity -> {
            AttrEntityResp attrEntityResp = new AttrEntityResp();
            BeanUtils.copyProperties(attrEntity, attrEntityResp);

            // 设置分类和分组的名字
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao
                        .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrEntityResp.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrEntityResp.setCatelogName(categoryEntity.getName());
            }

            return attrEntityResp;
        }).collect(Collectors.toList());

        pageUtils.setList(resps);
        return pageUtils;

    }

    @Override
    public AttrEntityResp getAttrInfo(Long attrId) {
        AttrEntityResp attrEntityResp = new AttrEntityResp();

        AttrEntity attrEntity = this.getById(attrId);

        BeanUtils.copyProperties(attrEntity, attrEntityResp);


        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 设置组id
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao
                    .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));

            if (attrAttrgroupRelationEntity != null) {
                attrEntityResp.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());

                // 设置组名称
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrEntityResp.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        // 设置菜单全路径
        attrEntityResp.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            attrEntityResp.setCatelogName(categoryEntity.getName());
        }


        return attrEntityResp;
    }

    @Transactional
    @Override
    public void updateAttr(AttrEntityVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.updateById(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 修改分组关联
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attr.getAttrGroupId());
            entity.setAttrId(attr.getAttrId());

            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(entity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(entity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> data = attrAttrgroupRelationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = data.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        if (attrIds != null && !CollectionUtils.isEmpty(attrIds)) {
            return this.listByIds(attrIds);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
//        attrAttrgroupRelationDao.delete(new QueryWrapper<>().eq("attr_id", 1L).eq("attr_group_id", 2L))

        List<AttrAttrgroupRelationEntity> data = Arrays.asList(vos).stream().map(vo -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());


        // 批量删除
        attrAttrgroupRelationDao.deletebatchrelation(data);


    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 一个分类包含多个属性分组
        // 一个属性分组包含多个属性

        // 1. 当前分组只能关联自己所属的分类里面的所有属性
        // 查询分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        // 分类id
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2. 当前分组只能关联别的分组没有引用的属性
        // 2.1 根据分类id查询所有属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 属性分组id集合
        List<Long> attrGroupIds = attrGroupEntities.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());

        // 2.2 从这些分组查询已关联的属性
        List<AttrAttrgroupRelationEntity> attrGroups = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        List<Long> attrIds = attrGroups.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        // 2.3 从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds != null && !CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn("attr_id", attrIds);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> w.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        List<AttrEntity> list = this.list(new QueryWrapper<AttrEntity>().in("attr_id", attrIds).eq("search_type", 1));
        return list.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
    }


}