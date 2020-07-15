package com.wxx.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 整合mybatis-plus
 *      1）、导入依赖
 *      <dependency>
 *          <groupId>com.baomidou</groupId>
 *          <artifactId>mybatis-plus-boot-starter</artifactId>
 *          <version>3.3.2</version>
 *      </dependency>
 *      2）、配置数据源
 *          1）、配置mysql驱动
 *          2）、配置数据源相关信息
 *      3）、配置Mybatis-Plus
 *          1）、配置MapperScan和dao蹭接口上加@Mapper二选一
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
