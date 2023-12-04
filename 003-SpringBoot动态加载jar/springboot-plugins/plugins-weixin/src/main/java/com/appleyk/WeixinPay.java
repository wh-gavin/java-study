package com.appleyk;
/**
 * <p>SPI - 微信App实现支付功能</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on 2022/11/23-9:52
 */
public class WeixinPay implements IPay{
    @Override
    public String pay() {
        return "weixin payed";
    }
}
