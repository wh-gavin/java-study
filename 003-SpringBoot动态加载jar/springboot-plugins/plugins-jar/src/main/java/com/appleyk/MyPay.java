package com.appleyk;

import com.appleyk.annotation.PluginOn;
import com.appleyk.model.User;

/**
 * <p>
 *     用户扩展支付能力
 *     下一个版本，插件的功能将变得复杂起来，这样才有意思
 * </p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on  下午9:35 2022/11/23
 */
@PluginOn
public class MyPay implements IPay{
    @Override
    public String pay() {
        User user = new User("appleyk","18");
        System.out.println("自定义的插件实现支付接口 -- v3！author: "+user.getName());
        return "自定义的插件实现支付接口 -- v3！";
    }
}
