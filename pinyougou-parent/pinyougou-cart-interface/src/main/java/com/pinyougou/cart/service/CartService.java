package com.pinyougou.cart.service;

import com.pinyougou.com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {
    /**
     * 商品添加到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中获取购物车对象列表
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 把购物车保存到redis中
     * @param cartList
     * @param username
     */
    void saveCartListToRedis(List<Cart> cartList,String username);

    /**
     * 合并购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);

}
