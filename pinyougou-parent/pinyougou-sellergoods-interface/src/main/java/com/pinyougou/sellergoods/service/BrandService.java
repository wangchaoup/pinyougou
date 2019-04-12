package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */

public interface BrandService {

    /**
     * 查找所有
     *
     * @return
     */
    List<TbBrand> findAll();

    /**
     *
     * 条件查询
     * @param //brand  品牌
     * @param pageNum  当前页数
     * @param pageSize 每页条数
     * @return
     */
    PageResult findPage(TbBrand brand,int pageNum, int pageSize);

    /**
     * 新建
     *
     * @param brand 品牌
     */
    void add(TbBrand brand);

    /**
     * 删除
     * @param ids id数组
     */
    void del(Long[] ids);

    /**
     * 查询单条数据
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand 品牌
     */
    void update(TbBrand brand);

    /**
     * 品牌下拉框
     * @return
     */
    List<Map> selectOptionList();

}
