package com.wxx.gulimall.search.service;

import com.wxx.gulimall.search.vo.SearchParam;
import com.wxx.gulimall.search.vo.SearchResult;

/**
 * @author 她爱微笑
 * @date 2020/11/3
 */
public interface MallSearchService {

    /**
     * 检索
     * @param searchParam /
     * @return 包含页面所需要的所有信息
     */
    SearchResult search(SearchParam searchParam);
}
