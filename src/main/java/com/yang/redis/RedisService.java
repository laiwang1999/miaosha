package com.yang.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    /**
     * 得到一个键的值
     *
     * @param prefix 前缀
     * @param key    键
     * @param clazz  得到值的对象类型
     * @param <T>    值的对象类型
     * @return 返回一个T对象（值）
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = StringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * set一个键值对
     *
     * @param prefix 前缀
     * @param key    键
     * @param value  值
     * @param <T>    值的对象类型
     * @return 返回一个boolean类型，代表成功或失败
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                //给str生成一个有效期
                jedis.setex(realKey, seconds, str);
            }
            jedis.set(realKey, str);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 对象转为字符串
     *
     * @param value Bean对象
     * @param <T>   需要转化的Bean对象类型
     * @return 返回对象转化后的字符串
     */
    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }

    }

    /**
     * 把字符串转化为一个bean对象
     *
     * @param str 字符串值
     * @param <T> 泛型类型，Bean
     * @return 返回一个Bean，
     */
    @SuppressWarnings("unchecked")
    private <T> T StringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 资源释放
     *
     * @param jedis 传入的jedis对象
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            //返回到连接池
            jedis.close();
        }
    }

    /**
     * 判断一个键是否存在
     *
     * @param prefix 前缀
     * @param key    键
     * @return 返回一个boolean类型，存在或不存在
     */
    public boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 执行原子加一操作
     *
     * @param prefix 前缀
     * @param key    键
     * @return 返回原来的值的原子加一
     */
    public Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 执行原子减一操作
     *
     * @param prefix 前缀
     * @param key    键
     * @return 返回原子减一后的结果
     */
    public Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }
}
