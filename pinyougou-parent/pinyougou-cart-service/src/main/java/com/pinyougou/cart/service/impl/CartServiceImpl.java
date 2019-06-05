package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.com.pinyougou.pojogroup.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 购物车中添加数据
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1、根据商品id查询商品列表（item）
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品已下架");
        }
        //2、根据商品获取商家id
        String sellerId = item.getSellerId();
        //3、查询购物车列表，判断是否有该商家的购物车对象
        //根据商家id进行查询
        Cart cart = findCart(cartList, sellerId);

        if (cart == null) {//4、如果没有该商家的购物车对象
            //4.1、创建该商家购物车对象
            cart = new Cart();
            //商家购物车中添加商家id
            cart.setSellerId(sellerId);
            //添加商家名称
            cart.setSellerName(item.getSeller());
            //添加订单详情
            TbOrderItem orderItem = createOrderItem(item, num);
            //添加订单详情到列表中
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            //4.2、把购物车对象添加到购物车对象列表中去
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        } else {//5、如果购物车对象列表中有该商家的购物车对象

            //查询该商家购物车商品明细列表(遍历)
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //判断商品订单是否存在
            if (orderItem == null) {
                //5.1、该商品不存在则添加到商品订单详情
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2、该商品存在，增加商品数量，总价
                //原基础上增加数量
                orderItem.setNum(orderItem.getNum() + num);
                //算出总价
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
                //如果订单中商品的数量小于等于零则移除该订单详情
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果商家购物车中没有订单则删除该商家购物车
                if (cart.getOrderItemList().size() <= 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * 从缓存中查找购物车对象
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从购物车中提取用户名：" + username);
        //从缓存中获取当前用户的购物车
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        //如果缓存中的购物车对象为空
        if (cartList == null) {
            //创建新的购物车对象列表
            cartList = new ArrayList();
        }
        return cartList;
    }

    /**
     * 购物车存入缓存
     *
     * @param cartList
     * @param username
     */
    @Override
    public void saveCartListToRedis(List<Cart> cartList, String username) {
        System.out.println("向缓存中存入购物车对象:" + username);
        //存入缓存
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    /**
     * 合并购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        System.out.println("合并购物车");
        //获取单个购物车对象
        for (Cart cart : cookieCartList) {
            //获取单条订单详情
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //把cookie中的购物车添加到缓存购物车对象订单中
                redisCartList = addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        //合并到缓存中
        return redisCartList;
    }

    /**
     * 判断商家购物车是否存在
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart findCart(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 创建订单对象
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        //创建订单列表对象
        TbOrderItem orderItem = new TbOrderItem();
        //设置各项属性值
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        //总价类型与单价类型不一致需要转换
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     * 判断订单明细是否存在
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }
}
