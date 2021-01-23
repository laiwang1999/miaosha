package com.yang.controller;

import com.yang.pojo.CodeMsg;
import com.yang.pojo.Result;
import com.yang.pojo.User;
import com.yang.redis.RedisService;
import com.yang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @GetMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,imooc");
    }

    @GetMapping("/helloError")
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "hello");
        return "hello";
    }

    @RequestMapping("/db/get/{id}")
    @ResponseBody
    public Result<User> doGet(@PathVariable("id") int id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTX(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<Long> redisGet(){
        Long v1 = redisService.get("key1",Long.class);
        return Result.success(v1);
    }
    @RequestMapping("redis/set")
    @ResponseBody
    public Result<String> redisSet(){
        boolean ret = redisService.set("key2", "hello redis");
        String str =  redisService.get("key2",String.class);
        return Result.success(str);
    }
}
