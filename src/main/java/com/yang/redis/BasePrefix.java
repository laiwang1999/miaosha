package com.yang.redis;

/**
 * 前缀的抽象类
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;
    private String prefix;

    /**
     * 构造函数,不传过期时间即为永不过期 (0)
     *
     * @param prefix 特定前缀
     */
    public BasePrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    /**
     * 构造函数
     *
     * @param expireSeconds 过期时间
     * @param prefix        前缀
     */
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * @return 过期时间，单位为s
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * @return 返回前缀，由类名与类型前缀拼接而成
     */
    @Override
    public String getPrefix() {
        //获取类名
        String className = this.getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
