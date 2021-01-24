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
    public Result<Boolean> doLogin(LoginVo loginVo) {
        log.info(loginVo.toString());
        //参数校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        //密码校验
        if(StringUtils.isNullOrEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        //手机号校验
        if(StringUtils.isNullOrEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        //登录
        CodeMsg codeMsg = miaoShaUserService.login(loginVo);
        if(codeMsg.getCode()==0){
            return Result.success(true);
        }else{
            return Result.error(codeMsg);
        }
    }

}
