package com.wxx.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/7/27
 */
@Data
public class MergeVO {

    /**
     * 整单id
     */
    private Long purchaseId;

    /**
     * 合并项集合
     */
    private List<Long> items;
}
