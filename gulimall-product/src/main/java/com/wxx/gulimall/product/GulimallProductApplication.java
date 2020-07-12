package com.wxx.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 *          1）、配置MapperScan
 */
@MapperScan("com.wxx.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
