package rocketmq.springboot.demo1;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * @description: 批量消息
 * 如果您每次只发送不超过4MB的消息，则很容易使用批处理，样例如下：
 * @author TAO
 * @date 2021/1/15 12:25
 */
public class BatchProducer {

    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("172.17.11.35:9876");
        //3.启动producer
        producer.start();
        List<Message> msgs = new ArrayList<Message>();
        //4.创建消息对象，指定主题Topic、Tag和消息体
        /**
         * 参数一：消息主题Topic
         * 参数二：消息Tag
         * 参数三：消息内容
         */
        Message msg1 = new Message("BatchTopic", "Tag1", ("Hello World" + 1).getBytes());
        Message msg2 = new Message("BatchTopic", "Tag1", ("Hello World" + 2).getBytes());
        Message msg3 = new Message("BatchTopic", "Tag1", ("Hello World" + 3).getBytes());

        msgs.add(msg1);
        msgs.add(msg2);
        msgs.add(msg3);
        //5.发送消息
        SendResult result = producer.send(msgs);
        //发送状态
        SendStatus status = result.getSendStatus();
        System.out.println("发送结果:" + result);
        //线程睡1秒
        TimeUnit.SECONDS.sleep(1);
        //6.关闭生产者producer
        producer.shutdown();
    }
}
