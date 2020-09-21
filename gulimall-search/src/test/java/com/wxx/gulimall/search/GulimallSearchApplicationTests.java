package com.wxx.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.wxx.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {

        System.out.println(client);
    }


    @Test
    void test() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("username", "zhangsan", "age", 18, "gender", "男");

        User user = new User();
        user.setUsername("zhangsan1111");
        user.setAge(18);
        user.setGender("女");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // 提取响应
        System.out.println(index);
    }

    @Test
    void test1() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));
        sourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
//        sourceBuilder.size(0);

        System.out.println(sourceBuilder.toString());

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse.toString());

        SearchHits hits = searchResponse.getHits();
        hits.forEach(hit -> {
            String s = hit.getSourceAsString();
            Account account = JSON.parseObject(s, Account.class);
            System.out.println(account);
        });

        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        ageAgg.getBuckets().forEach(bucket -> {
            String keyAsString = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println("年龄：" + keyAsString + ", 数量：" + docCount);
        });


        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均工资：" + balanceAvg.getValue());

    }


    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }

    @ToString
    @Data
    static class Account{
        private Integer account_number;
        private Integer balance;
        private String firstname;
        private String lastname;
        private Integer age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}
