package com.wxx.gulimall.cart.interceptor;

import com.wxx.common.constant.AuthServerConstant;
import com.wxx.common.constant.CartConstant;
import com.wxx.common.vo.MemberRespVO;
import com.wxx.gulimall.cart.vo.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author wangxin
 * @date 2020/11/22
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoDTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        MemberRespVO user = (MemberRespVO) attribute;
        if (user != null){
            // 用户登陆了
            userInfoDTO.setUsername(user.getUsername());
            userInfoDTO.setUserId(user.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoDTO.setUserKey(cookie.getValue());
                    userInfoDTO.setTempUser(true);
                }
            }
        }
        // 如果没有临时用户 则分配一个临时用户
        if (StringUtils.isEmpty(userInfoDTO.getUserKey())){
            String uuid = UUID.randomUUID().toString().replace("-","");
            userInfoDTO.setUserKey("FIRE-" + uuid);
        }
        threadLocal.set(userInfoDTO);
        return true;
    }

    /**
     * 执行完毕之后分配临时用户让浏览器保存
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoDTO userInfoDTO = threadLocal.get();
        if(!userInfoDTO.isTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoDTO.getUserKey());
            // 设置这个cookie作用域 过期时间
            cookie.setDomain("glmall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);
            response.addCookie(cookie);
        }
    }
}
