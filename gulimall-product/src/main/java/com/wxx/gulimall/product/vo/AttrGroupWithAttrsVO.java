package com.wxx.gulimall.product.vo;

import com.wxx.gulimall.product.entity.AttrEntity;
import com.wxx.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/7/26
 */
@Data
public class AttrGroupWithAttrsVO extends AttrGroupEntity {

    private List<AttrEntity> attrs;
}
