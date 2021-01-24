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
        //js的md5与java的md5不同，js采用2进制，java采用utf-8
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c"));
    }
}
