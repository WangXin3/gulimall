package com.wxx.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 *      4)、配置逻辑删除
 *          1)、配置文件开启逻辑删除，可以配置逻辑删除值和逻辑未删除值，和全局逻辑删除实体字段
 *              mybatis-plus:
 *                global-config:
 *                  db-config:
 *                    logic-delete-field: flag  # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
 *                    logic-delete-value: 1 # 逻辑已删除值(默认为 1)
 *                    logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
 *          2）、如果每个表逻辑删除字段不同，则可以单独配置
 *          @TableLogic(value = "1", delval = "0") 使用注解 也可以，单独配置逻辑删除值和非逻辑删除值
 *          private Integer deleted;
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wxx.gulimall.product.feign")
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
