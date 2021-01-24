package com.yang.service;

import com.yang.dao.MiaoshaUserDao;
import com.yang.pojo.CodeMsg;
import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.Result;
import com.yang.util.MD5Util;
import com.yang.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoShaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(Long id) {
        return miaoshaUserDao.getById(id);
    }

    /**
     * 登录方法
     *
     * @param loginVo 包含了mobile和password(经过md5加密后的)
     * @return 根据情况返回对应CodeMsg
     */
    public CodeMsg login(LoginVo loginVo) {
        if (loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        //通过一次加密后的formpass和盐值计算出密码，和数据库中的密码对比
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
