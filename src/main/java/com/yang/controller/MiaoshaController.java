package com.yang.controller;

import com.yang.pojo.CodeMsg;
import com.yang.pojo.MiaoshaOrder;
import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.OrderInfo;
import com.yang.redis.RedisService;
import com.yang.service.GoodsService;
import com.yang.service.MiaoShaUserService;
import com.yang.service.MiaoshaService;
import com.yang.service.OrderService;
import com.yang.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    MiaoShaUserService miaoShaUserService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String list(Model model,
                       MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        //库存不够
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单秒杀成功后直接进入订单的详情页
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }
}
