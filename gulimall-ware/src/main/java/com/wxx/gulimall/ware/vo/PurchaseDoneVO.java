package com.wxx.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/7/27
 */
@Data
public class PurchaseDoneVO {

    /**
     * 采购单id
     */
    @NotNull
    private Long id;

    private List<PurchaseItemDoneVO> items;
}
