package com.yang.controller;

import com.mysql.cj.util.StringUtils;
import com.yang.pojo.CodeMsg;
import com.yang.pojo.Result;
import com.yang.redis.RedisService;
import com.yang.service.MiaoShaUserService;
import com.yang.util.ValidatorUtil;
import com.yang.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {
    //log4f日志
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoShaUserService miaoShaUserService;

    /**
     * 前往登录页面
     *
     * @return 返回login界面
     */
    @RequestMapping("to_login")
    public String toLogin() {
        return "login";
    }

    /**
     * @param loginVo 建立的form表单传来参数的Vo
     * @return 如果参数校验都没问题，返回TRUE
     */
    @RequestMapping("do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
        miaoShaUserService.login(response,loginVo);
        return Result.success(true);
    }

}
