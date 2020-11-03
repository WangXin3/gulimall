package com.wxx.gulimall.search.controller;

import com.wxx.gulimall.search.service.MallSearchService;
import com.wxx.gulimall.search.vo.SearchParam;
import com.wxx.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 她爱微笑
 * @date 2020/11/2
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(@RequestBody SearchParam searchParam, Model model) {

        // 1.根据传递来的页面的查询参数，去es中检索
        SearchResult result = mallSearchService.search(searchParam);

        model.addAttribute("result", result);
        return "list";
    }

    @GetMapping("/search.html")
    public String search() {
        return "list";
    }
}
