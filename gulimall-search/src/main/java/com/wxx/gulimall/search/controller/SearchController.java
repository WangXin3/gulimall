package com.wxx.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 她爱微笑
 * @date 2020/11/2
 */
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }

    @GetMapping("/search.html")
    public String search() {
        return "list";
    }
}
