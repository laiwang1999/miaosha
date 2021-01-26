# 前期工作

## 环境配置

## 项目集成MyBatis
## Linux下载Redis

### 1.下载地址

https://redis.io/

![image-20210123151401578](https://img-blog.csdnimg.cn/img_convert/b86a0f391f66bfbea5c458e032f4c04e.png)

### 2.将压缩包解压并移动

```bash
tar -zxvf redis-6.0.10.tar.gz

mv redis-6.0.10 /usr/local/redis

cd /usr/lcoal/redis

make -j

make install 
```

### 3.修改配置文件

```bash
vim redis.conf

# bind 127.0.0.7代表本机，需要修改为你的公网IP
bind [IP]
# 端口号默认为6379
port 6379
# requirepass password 修改密码，默认没有密码
requirepass 123456
# daemonize no 修改为yes，代表允许后台执行
daemonize yes
# 退出并保存
:wq 
```

### 4.启动服务

```bash
redis-server ./redis.conf

# 查看redis是否启动成功
ps -ef | grep redis

```

### 5.访问redis

```bash
redis-cli
```

### 6.生成系统服务

```bash
cd /usr/local/redis/utils

./install_server.sh
# 之后会让选择一些东西，分别填入如下信息,第一个(port)和最后一个保持默认
/usr/local/redis/redis.conf
/usr/local/redis/redis.log
/usr/local/redis/data

# 如果出现错误，请注释掉76行到83行
```
## 集成Jedis

### 1. 引入jedis和fastjson依赖

```xml
<dependency>
   <groupId>redis.clients</groupId>
   <artifactId>jedis</artifactId>
</dependency>
<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>fastjson</artifactId>
   <version>1.2.74</version>
</dependency>
```

### 2. application.yml：填写配置信息

```yml
redis:  # redis配置
  host: IP
  port: 6379
  timeout: 3
  password: 123456
  poolMaxTotal: 10
  poplMaxIdle: 10
  pollMaxWait: 3
```

### 3. RedisConfig

```java
package com.yang.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;    //秒
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;//秒

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(int poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public int getPoolMaxWait() {
        return poolMaxWait;
    }

    public void setPoolMaxWait(int poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }
}
```

### 4. RedisPoolFactory

```java
package com.yang.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;
    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);

        JedisPool jp = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);
        return jp;
    }
}
```

### 5. RedisService

```java
package com.yang.redis;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.security.Key;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    /**
     * 得到一个键的值
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
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
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
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
     * @param value
     * @param <T>
     * @return
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
     * @param str
     * @param <T>
     * @return
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
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            //返回到连接池
            jedis.close();
        }
    }

    /**
     * 判断一个键是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key) {
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
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
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
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
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

```



# 用户登录-分布式Session
## 明文密码两次md5入库

### 1. 为什么需要两次md5加密？

1.用户端：Pass = MD5（明文+固定salt）

目的：防止被人恶意截取数据包，得到明文密码

2.服务端：Pass = MD5（用户输入+随机salt）

目的：万一数据库被盗，有可能通过反查表来得到密码

### 2. 引入MD5所需依赖

```xml
<dependency>
   <groupId>commons-codec</groupId>
   <artifactId>commons-codec</artifactId>
</dependency>
<dependency>
   <groupId>org.apache.commons</groupId>
   <artifactId>commons-lang3</artifactId>
   <version>3.10</version>
</dependency>
```

### 3. 创建md5加密工具类

```java
package com.yang.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    /**
     * md5加密
     *
     * @param src 对一个加salt的密码进行md5加密
     * @return 返回md5加密后的结果
     */
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    /**
     * 明文密码第一次进行md5加密
     *
     * @param inputPass 表单输入的密码
     * @return 返回加密后的结果
     */
    public static String inputPassFormPass(String inputPass) {
        String str = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * @param formPass 表单前端密码+salt 被md5加密后的密码
     * @return 返回加salt md5后的密码
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * @param input  表单输入
     * @param saltDB 数据库salt
     * @return 由表单输入经过两次md5加密后存入数据库
     */
    public static String inputPassToDbPass(String input, String saltDB) {
        String formPass = inputPassFormPass(input);
        String DBPass = formPassToDBPass(formPass, saltDB);
        return DBPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassFormPass("123456"));
        System.out.println(formPassToDBPass(inputPassFormPass("123456"), "1a2b3c4d"));
    }
}
```

## 登陆功能的实现



## 集成JSR303校验框架

### 引入依赖

```xml
<!-- JSR303依赖 -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

