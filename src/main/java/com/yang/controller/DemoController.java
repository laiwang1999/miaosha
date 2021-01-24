package com.yang.controller;

import com.yang.pojo.CodeMsg;
import com.yang.pojo.Result;
import com.yang.pojo.User;
import com.yang.redis.RedisService;
import com.yang.redis.UserKey;
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
    public Result<Boolean> dbTX() {
        userService.tx();
        return Result.success(true);
    }

    /**
     * @return 返回一个User对象
     */
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    /**
     * @return 返回一个boolean，代表成功失败
     */
    @RequestMapping("redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User(1, "11");
        boolean ret = redisService.set(UserKey.getById, "" + 1, user);//UserKey:id1
        return Result.success(ret);
    }
}
