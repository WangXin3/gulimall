package com.wxx.gulimall.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 她爱微笑
 * @date 2020/9/19
 */
@Configuration
public class GulimallElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;

    @Value("${elasticsearch.host}")
    public String ELASTICSEARCH_HOST;

    @Value("${elasticsearch.username}")
    public String ELASTICSEARCH_USERNAME;

    @Value("${elasticsearch.password}")
    public String ELASTICSEARCH_PASSWORD;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient() {
        /*用户认证对象*/
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        /*设置账号密码*/
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
        /*创建rest client对象*/
        RestClientBuilder builder = RestClient.builder(new HttpHost(ELASTICSEARCH_HOST,9200, "http"))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }


}
