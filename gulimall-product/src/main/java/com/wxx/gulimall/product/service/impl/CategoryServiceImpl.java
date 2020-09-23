package com.wxx.gulimall.product.service.impl;

import com.wxx.gulimall.product.service.CategoryBrandRelationService;
import com.wxx.gulimall.product.vo.Catalog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.Query;

import com.wxx.gulimall.product.dao.CategoryDao;
import com.wxx.gulimall.product.entity.CategoryEntity;
import com.wxx.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 组装成父子的树形结构
        List<CategoryEntity> level1Menus = entities.stream()
                // 过滤出需要的数据，有条件
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                // 处理集合中每条数据
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                })
                // 根据指定字段排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单，是否被别的菜单引用

        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        // 1. 查询所有分类
        List<CategoryEntity> category1s = this.getLevel1Categorys();

        // 2.封装数据
        Map<String, List<Catalog2VO>> map = category1s.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询每一个1级分类的2级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));

            List<Catalog2VO> catalog2VOList = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catalog2VOList = categoryEntities.stream().map(l2 -> {

                    List<CategoryEntity> category3Entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));

                    List<Catalog2VO.Catalog3VO> catalog3VOList = null;

                    if (!CollectionUtils.isEmpty(category3Entities)) {
                        catalog3VOList = category3Entities.stream().map(l3 -> {
                            Catalog2VO.Catalog3VO catalog3VO = new Catalog2VO.Catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                    }


                    Catalog2VO vo = new Catalog2VO(v.getCatId().toString(), catalog3VOList,
                            l2.getCatId().toString(), l2.getName());

                    return vo;
                }).collect(Collectors.toList());
            }

            return catalog2VOList;
        }));

        return map;
    }

    private List<Long> findParentPath(Long groupId, List<Long> paths) {
        // 1.收集当前节点id

        paths.add(groupId);
        CategoryEntity byId = this.getById(groupId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }

    /**
     * 查询所有子分类
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> childrenList = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        return childrenList;
    }

}