package com.pinyougou.page.service;

/**
 * 创建商品详情页接口
 */
public interface ItemPageService {

    /**
     * 生成静态页面
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除静态页面
     * @param ids
     * @return
     */
    boolean deleteItemHtml(Long[] ids);

}
