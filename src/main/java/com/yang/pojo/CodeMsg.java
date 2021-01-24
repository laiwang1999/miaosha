package com.yang.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeMsg {
    private int code;
    private String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    //登录模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或已失效");
    //密码为空
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "密码不能为空");
    //手机号为空
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    //手机号格式错误
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
    //用户不存在
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "用户不存在");
    //密码错误
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500214, "密码错误");
    //商品模块 5003XX

    //订单模块 5004XX

    //秒杀模块 5005XX
}
