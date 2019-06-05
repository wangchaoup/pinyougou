package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.com.pinyougou.pojogroup.Cart;
import com.pinyougou.common.utils.CookieUtil;
import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 3600)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 从cookie中查找购物车对象
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前用户为：" + username);
        //从cookie中获取json字符串
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //如果为空则设置为空数组
        if (cartListStr == null || cartListStr.equals("")) {
            cartListStr = "[]";
        }
        //获取购物车对象列表
        List<Cart> cartListCookie = JSON.parseArray(cartListStr, Cart.class);
        //判断是否登陆
        if ("anonymousUser".equals(username)) {//未登录从本地读取（cookie）
            return cartListCookie;
        } else {
            //已登录则从缓存中读取
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);

            //如果本地有数据，则把cookie和缓存中的数据合并
            if (cartListCookie.size() > 0) {
                //合并
                cartListFromRedis = cartService.mergeCartList(cartListCookie, cartListFromRedis);
                //清除cookie中数据
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入缓存
                cartService.saveCartListToRedis(cartListFromRedis, username);
            }
            return cartListFromRedis;
        }

    }

    /**
     * 添加数据到购物车中
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9105")
    //allowCredentials="true" 属性为缺省配置（默认）
    public Result addGoodsToCartList(Long itemId, Integer num) {

        /*//跨域请求
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");*/
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前用户为：" + username);
        try {
            //查找购物车对象
            List<Cart> cartList = findCartList();
            //调用方法添加购物车对象列表并返回
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //判断用户是否登陆
            if ("anonymousUser".equals(username)) {
                //未登录，购物车保存到cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600, "UTF-8");
                System.out.println("向cookie中存数据");
            } else {
                //已登录，添加到缓存中
                cartService.saveCartListToRedis(cartList, username);
            }
            return new Result(true, "添加成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
