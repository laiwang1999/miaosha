package com.yang.controller;

import com.mysql.cj.util.StringUtils;
import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.User;
import com.yang.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoShaUserService miaoShaUserService;
    /**
     *
     * @param model 用于向页面传值
     * @param cookieToken 电脑端从Cookie中拿取token
     * @param paramToken 为了兼容手机端，手机与电脑存放token的地方可能不同，手机端从参数获取token
     * @return 返回商品页面
     */
    @RequestMapping("/to_list")
    public String toList(Model model,
                         @CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                         @RequestParam(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken) {
        //如果没有获取到token,直接返回登录页面
        if (StringUtils.isNullOrEmpty(cookieToken) && StringUtils.isNullOrEmpty(paramToken)) {
            return "login";
        }
        //拿到token
        String token = StringUtils.isNullOrEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser user = miaoShaUserService.getByToken(token);
        model.addAttribute("user",user);
        return "goods_list";
    }
}
