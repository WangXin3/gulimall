package com.wxx.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.wxx.common.dto.SkuReductionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wxx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.wxx.gulimall.coupon.service.SkuFullReductionService;
import com.wxx.common.utils.PageUtils;
import com.wxx.common.utils.R;



/**
 * 商品满减信息
 *
 * @author wangxin
 * @email 91907@163.com
 * @date 2020-07-12 22:00:55
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @PostMapping("/saveInfo")
    public R saveInfo(@RequestBody SkuReductionDTO skuReductionDTO){
        skuFullReductionService.saveSkuReduction(skuReductionDTO);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:skufullreduction:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:skufullreduction:info")
    public R info(@PathVariable("id") Long id){
		SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:skufullreduction:save")
    public R save(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:skufullreduction:update")
    public R update(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:skufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
		skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
