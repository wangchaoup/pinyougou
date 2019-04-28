package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.com.pinyougou.pojogroup.Goods;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        //根据当前登陆用户来进行商品添加，标识查找作用
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置商家id
        goods.getGoods().setSellerId(name);
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        //对当前用户进行校验，如果当前商家id与商品所传递的商家id不一致则无法进行修改操作

        //查找商品，然后查找其卖家id
        Long id = goods.getGoods().getId();
        Goods one = goodsService.findOne(id);
        String sellerId1 = one.getGoods().getSellerId();
        //当前登陆用户id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //验证不通过直接返回,双重验证
        if (!sellerId1.equals(name) || !name.equals(goods.getGoods().getSellerId())) {
        return new Result(false,"非法操作");
        }
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        //获取当前登陆的用户名（用户id）
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置该商品的商家id
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

    @RequestMapping("/updateMarketStatus")
    public Result updateMarketStatus(Long[] ids,String marketStatus){
        try {
            goodsService.updateMarketStatus(ids,marketStatus);
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
