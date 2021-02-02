package com.yang.dao;

import com.yang.pojo.Goods;
import com.yang.pojo.MiaoshaGoods;
import com.yang.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao {
    /**
     * 返回所有的商品信息
     *
     * @return 返回一个GoodsVo列表
     */
    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date, mg.miaosha_price\n" +
            "from goods as g\n" +
            "         left join miaosha_goods as mg on mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    /**
     * 根据商品ID查出商品信息
     *
     * @param goodsId 商品ID
     * @return 返回一个GoodsVo对象
     */
    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date, mg.miaosha_price\n" +
            "from goods as g\n" +
            "         left join miaosha_goods as mg on mg.goods_id = g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} ")
    int reduceStock(MiaoshaGoods g);
}
