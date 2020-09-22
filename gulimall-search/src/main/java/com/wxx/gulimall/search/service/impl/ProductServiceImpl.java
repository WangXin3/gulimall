package com.wxx.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.wxx.common.dto.es.SkuEsModel;
import com.wxx.gulimall.search.config.GulimallElasticSearchConfig;
import com.wxx.gulimall.search.constant.EsConstant;
import com.wxx.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 她爱微笑
 * @date 2020/9/22
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private RestHighLevelClient client;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        // 保存到es
        // 1. 给es建立索引 product

        // 2. 给es中保存数据
        BulkRequest bulkRequest = new BulkRequest();

        skuEsModels.forEach(skuEsModel -> {
            // 3. 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String json = JSON.toJSONString(skuEsModel);
            indexRequest.source(json, XContentType.JSON);

            bulkRequest.add(indexRequest);
        });

        BulkResponse bulk = client.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();

        List<String> list = Arrays.stream(bulk.getItems()).map(bb -> bb.getId()).collect(Collectors.toList());
        log.error("商品上架成功：{}", list);

        return b;
    }
}
