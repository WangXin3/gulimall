package com.wxx.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wxx.gulimall.product.service.CategoryBrandRelationService;
import com.wxx.gulimall.product.vo.Catalog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

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

    /**
     * 本地锁
     * @return
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithLocalLock() {
        // 加锁
        // TODO 本地锁，在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            return getDataFromDB();
        }
    }

    /**
     * 分布式锁， 基于redis
     * @return
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        // 加锁设置过期时间（原子性操作） 避免死锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // 加锁成功 做业务
            Map<String, List<Catalog2VO>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDB();
            } finally {
                // 删除锁 保证原子性操作，使用lua脚本
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                // 删除成功返回1 失败返回0
                Long delValue = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
            }


            return dataFromDB;
        } else {
            // 加锁失败 重试 （自旋锁）
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return getCatalogJsonFromDbWithRedisLock();
        }


    }

    private Map<String, List<Catalog2VO>> getDataFromDB() {
        // 再去缓存中查询结果，如果没有再去查询db
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
        }

        // 查询所有分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);


        // 1. 查询所有1级分类
        List<CategoryEntity> category1s = this.getParentCid(selectList, 0L);

        // 2.封装数据
        Map<String, List<Catalog2VO>> map = category1s.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询每一个1级分类的2级分类
            List<CategoryEntity> categoryEntities = this.getParentCid(selectList, v.getCatId());

            List<Catalog2VO> catalog2VOList = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catalog2VOList = categoryEntities.stream().map(l2 -> {

                    List<CategoryEntity> category3Entities = this.getParentCid(selectList, l2.getCatId());

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

        // 3.并放入缓存
        redisTemplate.opsForValue().set("catalogJSON", JSON.toJSONString(map), 1, TimeUnit.DAYS);

        return map;
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        Map<String, List<Catalog2VO>> map;

        /**
         * 1. 空结果缓存，解决缓存穿透
         * 2. 在设置过期时间时 加上随机时长（1-5分钟），解决缓存雪崩
         * 3. 给db请求加锁，避免缓存击穿
         */

        // 1.加入缓存逻辑
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2.缓存中没有, 查询数据库
            map = getCatalogJsonFromDbWithRedisLock();
        } else {
            map = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
        }

        return map;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long cid) {
        return selectList.stream().filter(item -> item.getParentCid().equals(cid)).collect(Collectors.toList());
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