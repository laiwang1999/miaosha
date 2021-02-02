package com.yang.service;


import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.OrderInfo;
import com.yang.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {
    //一般在service中只引入自己的Dao，如果要引入其他的dao，需要引入它的service
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    /**
     * 开启事务
     * @param user 秒杀用户
     * @param goods 商品信息
     * @return 秒杀成功后返回一个订单信息
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减少库存，下订单，写入秒杀订单

        //减少库存
        int i = goodsService.reduceStock(goods);
        System.out.println(i);
        //生成订单
        return orderService.createOrder(user,goods);
    }
}

