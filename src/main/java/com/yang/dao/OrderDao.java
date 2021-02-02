package com.yang.dao;

import com.yang.pojo.MiaoshaOrder;
import com.yang.pojo.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);

    /**
     * 添加订单信息
     *
     * @param orderInfo 需要插入的订单信息
     * @return 返回订单ID
     */
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count,\n" +
            "                       goods_price, order_channel, status, create_date\n" +
            "                       )\n" +
            "                       values (#{userId},#{goodsId},#{goodsName},#{goodsCount}," +
            "                       #{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 添加秒杀订单信息
     *
     * @param miaoshaOrder 需要添加的秒杀订单信息
     * @return 返回一个整数代表成功添加
     */
    @Insert("insert into miaosha_order(user_id,goods_id,order_id)" +
            " values(#{userId},#{goodsId},#{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
