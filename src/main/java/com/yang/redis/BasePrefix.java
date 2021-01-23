package com.yang.redis;

public abstract class BasePrefix implements KeyPrefix {

    @Override
    public int expireSeconds() {
        return 0;
    }

    @Override
    public String getPrefix() {
        return null;
    }
}
