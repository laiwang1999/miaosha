package com.yang.redis;

/**
 * redis键的前缀接口
 */
public interface KeyPrefix {
    int expireSeconds();

    String getPrefix();

}
