package com.wxx.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2级分类vo
 * @author 她爱微笑
 * @date 2020/9/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2VO {

    /**
     * 1级父分类id
     */
    private String catalog1Id;

    /**
     * 三级子分类
     */
    private List<Catalog3VO> catalog3List;

    private String id;

    private String name;

    /**
     * 三级分类vo
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catalog3VO {
        /**
         * 2级父分类id
         */
        private String catalog2Id;

        private String id;

        private String name;

    }
}
