package com.wxx.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxx.gulimall.product.entity.BrandEntity;
import com.wxx.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

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



}
