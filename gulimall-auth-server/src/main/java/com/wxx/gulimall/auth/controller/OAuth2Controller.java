package com.wxx.gulimall.auth.controller;

import cn.hutool.core.lang.Singleton;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wxx.common.constant.AuthServerConstant;
import com.wxx.common.utils.R;
import com.wxx.common.vo.MemberRespVO;
import com.wxx.gulimall.auth.feign.MemberFeignService;
import com.wxx.gulimall.auth.singleton.ParserConfigSingleton;
import com.wxx.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@Controller
@Slf4j
public class OAuth2Controller {

    @Value("${oAuth2.weibo.clientId}")
    private String clientId;

    @Value("${oAuth2.weibo.clientSecret}")
    private String clientSecret;

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 微博登录回调
     * @return /
     */
    @GetMapping("/oauth2/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse httpServletResponse) {
        Map<String, Object> body = new HashMap<>(5);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", "http://auth.gulimall.com/oauth2/weibo/success");
        body.put("code", code);


        // 1.根据code获取accessToken
        HttpResponse response = HttpRequest.post("https://api.weibo.com/oauth2/access_token").form(body).execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            // 2.根据accessToken获取用户信息
            // 生产环境中，parserConfig要做singleton处理，要不然会存在性能问题
            ParserConfigSingleton singleton = Singleton.get(ParserConfigSingleton.class);
            SocialUser socialUser = JSON.parseObject(response.body(), SocialUser.class,
                    singleton.getUnderscore2HumpParserConfig());

            // 3.如果用户第一次登录，则给为当前用户自动注册;如果是老用户，调用和本系统绑定的用户
            R r = memberFeignService.oauthLogin(socialUser);
            if (r.getCode() == 0) {
                // 登录成功， 提取用户信息
                MemberRespVO data = r.getData(new TypeReference<MemberRespVO>() {
                });
                // 指定域名为父域名
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                log.info("登录成功:{}", data.toString());
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://gulimall.com/login.html";
            }
        } else {
            return "redirect:http://gulimall.com/login.html";
        }

    }
}
