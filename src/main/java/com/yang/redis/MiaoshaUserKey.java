package com.yang.redis;

/**
 * 生成token的键
 */
public class MiaoshaUserKey extends BasePrefix {
    //token有效期
    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
}
