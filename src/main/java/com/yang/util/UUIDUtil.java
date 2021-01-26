package com.yang.util;

import java.util.UUID;

/**
 * 生成UUID
 */
public class UUIDUtil {
    public static String uuid(){
        //把UUID中的`-`去掉
        return UUID.randomUUID().toString().replace("-","");
    }
}
