package com.wxx.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxx.gulimall.product.entity.BrandEntity;
import com.wxx.gulimall.product.service.AttrGroupService;
import com.wxx.gulimall.product.service.BrandService;
import com.wxx.gulimall.product.service.CategoryService;
import com.wxx.gulimall.product.service.SkuSaleAttrValueService;
import com.wxx.gulimall.product.vo.SkuItemSaleAttrVO;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;


    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    public void test() {
//        List<SpuItemAttrGroupVO> attrGroupsVOs = attrGroupService.getAttrGroupWithAttrsBySpuId(8L, 225L);
//
//        System.out.println(attrGroupsVOs);


        List<SkuItemSaleAttrVO> saleAttrVos = skuSaleAttrValueService.listSaleAttrs(8L);
        System.out.println(saleAttrVos);

    }

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//
//        brandService.save(brandEntity);
//        System.out.println("保存成功");

//        brandEntity.setBrandId(1L);
//        brandEntity.setName("苹果");
//        brandEntity.setDescript("好手机");
//        brandService.updateById(brandEntity);

        brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L)).forEach(
                System.out::println
        );


    }

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        System.out.println(Arrays.asList(catelogPath));
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        stringRedisTemplate.boundValueOps("abc").set("123");

    }

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }

}
