package com.wxx.gulimall.search.service;

import com.wxx.common.dto.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author 她爱微笑
 * @date 2020/9/22
 */
public interface ProductService {
    Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

}
