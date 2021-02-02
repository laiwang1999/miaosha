package com.yang.controller;

import com.mysql.cj.util.StringUtils;
import com.yang.pojo.MiaoshaUser;
import com.yang.pojo.User;
import com.yang.service.GoodsService;
import com.yang.service.MiaoShaUserService;
import com.yang.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    GoodsService goodsService;

    /**
     * 通过WebMvcConfiguration简化了参数,详情见WebConfig
     *
     * @param model       用于向页面传值
     * @return 返回商品页面
     */
    @RequestMapping("/to_list")
    public String toList(//HttpServletResponse response,
                         Model model,
                         //@CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                         //@RequestParam(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String paramToken,
                         MiaoshaUser user) {
        //如果没有获取到token,直接返回登录页面
//        if (StringUtils.isNullOrEmpty(cookieToken) && StringUtils.isNullOrEmpty(paramToken)) {
//            return "login";
//        }
//        //拿到token
//        String token = StringUtils.isNullOrEmpty(paramToken) ? cookieToken : paramToken;
//        MiaoshaUser user = miaoShaUserService.getByToken(token, response);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("user", user);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model,
                           MiaoshaUser user,
                           @PathVariable("goodsId") long goodsId) {
        //snowflake
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        //当前时间
        long now = System.currentTimeMillis();
        System.out.println(startAt+" "+endAt+" "+now);
        model.addAttribute("goods",goods);
        //秒杀状态
        int miaoshaStatus = 0;
        //距离秒杀开始还剩多长时间
        int remainSeconds = 0;
        if (now < startAt) {//0 表示秒杀还没开始,倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//2 表示秒杀结束了
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀正在进行中 1
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }
}
