package com.wxx.gulimall.ware.vo;

import lombok.Data;

/**
 * @author 她爱微笑
 * @date 2020/7/27
 */
@Data
public class PurchaseItemDoneVO {
    /**
     * itemId:1,status:4,reason:""
     */

    private Long itemId;
    private Integer status;
    private String reason;
}
