package com.yang.redis;

public class UserKey extends BasePrefix {
    /**
     * 最好设置为private，外部可以访问内部的共有静态方法来调用构造方法，提高安全性
     * @param prefix 用户类的前缀
     */
    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
