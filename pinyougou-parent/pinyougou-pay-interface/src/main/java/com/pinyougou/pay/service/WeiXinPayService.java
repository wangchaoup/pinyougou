package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeiXinPayService {

    /**
     * 生成二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map createNative(String out_trade_no,String total_fee);
}
