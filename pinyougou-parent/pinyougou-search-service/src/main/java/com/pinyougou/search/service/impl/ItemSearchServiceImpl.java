package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据关键字检索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {

        //关键字去空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        Map<String, Object> map = new HashMap<>();
        //高亮查询
        map.putAll(searchList(searchMap));
        //根据关键字查询
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        //是否有分类id
        if (!"".equals(categoryName)) {
            map.putAll(searchBrandSpeclist(categoryName));
        } else {
            //如果没有分类名称，则按照第一个查询
            if (categoryList.size() > 0) {
                map.putAll(searchBrandSpeclist(categoryList.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        //保存到索引库
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        //删除
        Query query = new SimpleQuery();
        Criteria itemGoodsId = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(itemGoodsId);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    /**
     * 检索，关键字高亮
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        //创建存储容器
        Map map = new HashMap();
        //创建查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮域字段的html前缀标签
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //后缀标签
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //按照分类筛选
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按品牌筛选
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //按照价格过滤
        if (!"".equals(searchMap.get("price"))){
            //获取价格字符串并分割
            String[] prices = ((String) searchMap.get("price")).split("-");
            //查询价格下限以上结果，查询方法会把String类型转换为long类型
            if (!"0".equals(prices[0])){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //上限以下结果
            if (!prices[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //分页查询
        //提取页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null){
            //默认第一页
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize== null){
            //设置每页默认显示条数
            pageSize=30;
        }
        //从第几条开始查询
        query.setOffset((pageNo-1)*pageSize);
        //查询条数
        query.setRows(pageSize);

        //价格排序
        //获取排序方式
        String sortValue = (String) searchMap.get("sort");
        //获取排序字段
        String sortField = (String) searchMap.get("sortField");
        if (sortValue != null && !sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //查询高亮入口
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            //获取原实体类
            TbItem item = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        //返回总条数
        map.put("total",page.getTotalElements());
        //返回总页数
        map.put("totalPages",page.getTotalPages());
        return map;
    }


    /**
     * 查询分类列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    private Map searchBrandSpeclist(String category) {
        Map map = new HashMap();
        //获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            //根据模板id查询品牌
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
}
