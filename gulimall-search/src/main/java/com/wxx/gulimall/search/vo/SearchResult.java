package com.wxx.gulimall.search.vo;

import com.wxx.common.dto.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/11/3
 */
@Data
public class SearchResult {

    /**
     * 所有商品信息
     */
    private List<SkuEsModel> products;


    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long totalPages;

    /**
     * 当前查询到的结果，涉及到的所有品牌
     */
    private List<BrandVO> brands;

    /**
     * 当前查询到的结果，涉及到的所有分类
     */
    private List<CatalogVO> catalogs;

    /**
     * 当前查询到的结果，涉及到的所有属性
     */
    private List<AttrVO> attrs;



    @Data
    public static class BrandVO {
        private Long brandId;

        private String brandName;

        private String brandImg;

    }

    @Data
    public static class CatalogVO {
        private Long catalogId;

        private String catalogName;
    }

    @Data
    public static class AttrVO {
        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }
}
