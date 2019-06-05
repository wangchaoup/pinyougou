package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.utils.HttpClient;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    //公众账号ID
    @Value("${appid}")
    private String appid;

    //商户号
    @Value("${partner}")
    private String partner;

    //密钥
    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成二维码
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //封装参数
        Map<String, String> parm = new HashMap();
        parm.put("appid", appid);  //公众账号ID
        parm.put("mch_id", partner); //商户号
        parm.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串
        parm.put("body", "品优购"); //商品描述
        parm.put("out_trade_no", out_trade_no); //商户订单号
        parm.put("total_fee", total_fee); //标价金额
        parm.put("spbill_create_ip", "127.0.0.1"); //终端IP
        parm.put("notify_url", "http://www.weixin.qq.com/wxpay/pay.php"); //通知地址
        parm.put("trade_type", "NATIVE"); //交易类型
        //发送请求
        try {
            //生成xml文件
            String signedXml = WXPayUtil.generateSignedXml(parm, partnerkey);
            System.out.println(signedXml);
            //发送sml文件
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //https传输
            httpClient.setHttps(true);
            //添加参数
            httpClient.setXmlParam(signedXml);
            //发送请求的方式
            httpClient.post();

            //返回结果

            String result = httpClient.getContent();
            System.out.println(result);
            //xml转换为map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            //最后需要返回订单号，总金额，二维码链接(其余信息不需要返回)
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url")); //二维码链接
            map.put("total_fee", total_fee); //总金额
            map.put("out_trade_no", out_trade_no);//商户订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }


    }
}
