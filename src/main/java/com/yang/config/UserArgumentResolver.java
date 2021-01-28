package com.yang.config;

import com.mysql.cj.util.StringUtils;
import com.yang.pojo.MiaoshaUser;
import com.yang.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    MiaoShaUserService miaoShaUserService;

    /**
     * @param methodParameter 通过methodParameter获取
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //获取参数类型
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    /**
     * @param methodParameter       通过methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest      得到request对象
     * @param webDataBinderFactory
     * @return 返回redis中存储的对象
     * @throws Exception 所有异常
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        //手机端cookie
        String paramToken = request.getParameter(MiaoShaUserService.COOKIE_NAME_TOKEN);
        //电脑端cookie
        String cookieToken = getCookieValue(request, MiaoShaUserService.COOKIE_NAME_TOKEN);
        //如果cookie都为空，返回一个空，
        if (StringUtils.isNullOrEmpty(paramToken) && StringUtils.isNullOrEmpty(cookieToken)) {
            return null;
        }
        String token = StringUtils.isNullOrEmpty(paramToken) ? cookieToken : paramToken;
        return miaoShaUserService.getByToken(token, response);
    }

    /**
     * 从所有cookie中获取我们需要的那个
     *
     * @param request         得到所有cookie
     * @param cookieNameToken 想要得到的cookie的名字
     * @return 返回需要的cookie
     */
    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
