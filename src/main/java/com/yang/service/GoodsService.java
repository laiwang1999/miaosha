package com.yang.service;

import com.yang.dao.GoodsDao;
import com.yang.pojo.Goods;
import com.yang.pojo.MiaoshaGoods;
import com.yang.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    /**
     * 获取所有商品信息
     *
     * @return 返回一个List<GoodsVo>
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    /**
     * 通过商品ID来获取一个商品信息
     *
     * @param goodsId 商品Id
     * @return 返回一个商品信息
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setId(goods.getId());
        goodsDao.reduceStock(g);
    }
}
