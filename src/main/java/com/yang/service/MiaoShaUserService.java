package com.yang.service;

import com.mysql.cj.util.StringUtils;
import com.yang.dao.MiaoshaUserDao;
import com.yang.exception.GlobalException;
import com.yang.exception.GlobalExceptionHandler;
import com.yang.pojo.CodeMsg;
import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.Result;
import com.yang.redis.MiaoshaUserKey;
import com.yang.redis.RedisService;
import com.yang.util.MD5Util;
import com.yang.util.UUIDUtil;
import com.yang.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {
    //Cookie中存放token的名称
    public static final String COOKIE_NAME_TOKEN = "token";
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RedisService redisService;

    /**
     * 通过id得到对象
     *
     * @param id 秒杀用户的id
     * @return 返回一个秒杀用户对象
     */
    public MiaoshaUser getById(Long id) {
        return miaoshaUserDao.getById(id);
    }

    /**
     * 登录方法
     *
     * @param loginVo 包含了mobile和password(经过md5加密后的)
     * @return 根据情况返回对应CodeMsg
     */
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        //通过一次加密后的formpass和盐值计算出密码，和数据库中的密码对比
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.MOBILE_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(miaoshaUser, token, response);
        return true;
    }

    /**
     * 生成Cookie
     *
     * @param user     MiaoshaUser对象
     * @param response HttpServletResponse对象
     * @param token    token
     */
    private void addCookie(MiaoshaUser user, String token, HttpServletResponse response) {
        //生成cookie
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //cookie的有效期
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 得到token从redis缓存中得到用户
     *
     * @param token 用户表示
     * @return 返回一个从redis中得到的对象
     */
    public MiaoshaUser getByToken(String token, HttpServletResponse response) {
        if (StringUtils.isNullOrEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (miaoshaUser != null) {
            addCookie(miaoshaUser, token, response);
        }

        return miaoshaUser;
    }
}
