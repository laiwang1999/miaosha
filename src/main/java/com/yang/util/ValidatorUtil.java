package com.yang.util;

import com.mysql.cj.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数校验类
 */
public class ValidatorUtil {
    //正则表达式规则
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    /**
     * 手机号校验
     * @param src 手机号
     * @return 返回boolean类型参数，代表是或不是
     */
    public static boolean isMobile(String src) {
        if (StringUtils.isNullOrEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }

}
