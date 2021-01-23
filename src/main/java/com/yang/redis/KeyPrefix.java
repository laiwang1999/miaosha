package com.yang.redis;

public interface KeyPrefix {
    int expireSeconds();

    String getPrefix();

}
