package com.wxx.gulimall.search.controller;

import com.wxx.common.dto.es.SkuEsModel;
import com.wxx.common.exception.BizCodeEnum;
import com.wxx.common.utils.R;
import com.wxx.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/9/22
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        Boolean aBoolean = null;
        try {
            aBoolean = productService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误: {}", e);
            return R.error(BizCodeEnum.PRODUCT_UP.getCode(), BizCodeEnum.PRODUCT_UP.getMsg());
        }

        if (aBoolean) {
            return R.error(BizCodeEnum.PRODUCT_UP.getCode(), BizCodeEnum.PRODUCT_UP.getMsg());
        } else {
            return R.ok();
        }
    }
}
