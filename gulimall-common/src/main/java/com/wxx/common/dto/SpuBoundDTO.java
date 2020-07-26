package com.wxx.common.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 她爱微笑
 * @date 2020/7/26
 */
@Data
public class SpuBoundDTO {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
