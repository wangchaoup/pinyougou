package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.ReturnResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }



    /**
     * 分页查询所有
     * 条件查询
     * @param brand
     * @param page
     * @param size
     * @RequestBody 该注释表示请求方式为post
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody TbBrand brand, int page, int size) {
        return brandService.findPage(brand,page, size);
    }

    /**
     * 新建
     *
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public ReturnResult add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new ReturnResult(true, "新建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnResult(false, "创建失败");
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/del")
    public ReturnResult del(Long[] ids){
       try {
           brandService.del(ids);
           return new ReturnResult(true,"删除成功");
       }catch (Exception e){
           e.printStackTrace();
           return new ReturnResult(false,"删除失败");
       }
    }

    /**
     * 查找单条数据
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public ReturnResult update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new ReturnResult(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnResult(false,"修改失败");
        }
    }
}
