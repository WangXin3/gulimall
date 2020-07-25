package com.wxx.gulimall.product.vo;


import lombok.Data;

/**
 * @author 她爱微笑
 * @date 2020/7/25
 */
@Data
public class AttrEntityResp extends AttrEntityVO {

    /**
     * "手机/数码/手机", //所属分类名字
     */
    private String catelogName;

    /**
     * 主体", //所属分组名字
     */
    private String groupName;

    /**
     * [2, 34, 225] //分类完整路径
     */
    private Long[] catelogPath;

}
