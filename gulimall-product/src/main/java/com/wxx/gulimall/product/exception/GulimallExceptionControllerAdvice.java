package com.wxx.gulimall.product.exception;

import com.wxx.common.exception.BizCodeEnum;
import com.wxx.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 她爱微笑
 * @date 2020/7/19
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.wxx.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题: {}, 异常类型: {}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();

        // 获取校验的错误结果
        Map<String, String> map = new HashMap<>(2);

        bindingResult.getFieldErrors().forEach(
                fieldError -> {
                    // 获取到错误提示
                    String defaultMessage = fieldError.getDefaultMessage();
                    // 获取到错误的属性名字
                    String field = fieldError.getField();
                    map.put(field, defaultMessage);
                }
        );

        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }


    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg()).put("errorInfo", throwable.getMessage());
    }
}
