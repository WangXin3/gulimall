package com.wxx.gulimall.product.web;

import com.wxx.gulimall.product.service.SkuInfoService;
import com.wxx.gulimall.product.vo.SkuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wangxin
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        System.out.println("准备查询" + skuId + "详情");

        SkuItemVO skuItemVO = skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVO);

        return "item";
    }
}
