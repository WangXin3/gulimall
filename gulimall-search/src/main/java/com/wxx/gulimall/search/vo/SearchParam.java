package com.wxx.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * @author 她爱微笑
 * @date 2020/11/3
 */
@Data
public class SearchParam {

    /**
     * 全文匹配字段
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序字段_asc/desc
     * saleCount_asc/desc
     * skuPrice_asc/desc
     * hotScore_asc/desc
     */
    private String sort;

    /**
     * 是否有货 0-有货 1-无货
     */
    private String hasStock;


    /**
     * 商品价格区间
     * 1_500 1到500
     * _500 小于500
     * 500_ 大于500
     */
    private String skuPrice;

    /**
     * 品牌id
     */
    private List<Long> brandId;


    /**
     * attrs=1_其他:安卓:IOS&attrs=2_6G:8G
     * 属性筛选条件
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Long pageNum;

}
