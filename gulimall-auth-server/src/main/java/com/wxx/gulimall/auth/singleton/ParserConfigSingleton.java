package com.wxx.gulimall.auth.singleton;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;

/**
 * @author wangxin
 * @date 2020/11/21
 */
public class ParserConfigSingleton {

    public ParserConfig getUnderscore2HumpParserConfig() {
        ParserConfig underscore2HumpParserConfig = new ParserConfig();
        underscore2HumpParserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return underscore2HumpParserConfig;
    }
}
