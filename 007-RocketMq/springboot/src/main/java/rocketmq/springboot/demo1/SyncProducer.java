package rocketmq.springboot.demo1;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.util.concurrent.TimeUnit;

/**
 * @description: 发送同步消息
 * 这种可靠性同步地发送方式使用的比较广泛，比如：重要的消息通知，短信通知。
 * @author TAO
 * @date 2021/1/14 20:52
 */
public class SyncProducer {

    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("172.17.11.35:9876");
        producer.setRetryTimesWhenSendFailed(1); 
        
        // 设置同步发送失败时重试发送的次数，默认为 2 次 
        //producer.setRetryTimesWhenSendFailed(3); 
        // 设置发送超时时限为 5s，默认 3s 
        //producer.setSendMsgTimeout(5000);
        
        //3.启动producer
        producer.start();

        for (int i = 0; i < 3; i++) {
            //4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("first-topic", null, ("Hello World" + i).getBytes());
            //5.发送消息
            SendResult result = producer.send(msg);
            //发送状态
            SendStatus status = result.getSendStatus();
            System.out.println("发送结果:" + result);
            //线程睡1秒
            TimeUnit.SECONDS.sleep(1);
        }
        //6.关闭生产者producer
        producer.shutdown();
    }
}

