package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            //转换为字符串格式
            TextMessage textMessage =(TextMessage)message;
            //读取消息内容
            String text = textMessage.getText();
            //拿到商品条目对象
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);

            for (TbItem item : itemList) {
                //将spec字段中的json转换为map
                Map specMap = JSON.parseObject(item.getSpec());
                //给带注解的字段赋值
                item.setSpecMap(specMap);
            }
            //导入solr
            itemSearchService.importList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
