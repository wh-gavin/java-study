package rocketmq.springboot.demo1;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * @description: 单向发送消息
 * 这种方式主要用在不特别关心发送结果的场景，例如日志发送。
 * @author TAO
 * @date 2021/1/14 21:40
 */
public class OneWayProducer {

    public static void main(String[] args) throws Exception, MQBrokerException {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("172.17.11.35:9876");
        //集群模式
        //producer.setNamesrvAddr("192.168.0.188:9876;192.168.0.177:9876");
        //3.启动producer
        producer.start();

        for (int i = 0; i < 3; i++) {
            //4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("first-topic", null, ("Hello World，单向消息" + i).getBytes());
            //5.发送单向消息
            producer.sendOneway(msg);
            //线程睡1秒
            TimeUnit.SECONDS.sleep(5);
        }
        //6.关闭生产者producer
        producer.shutdown();
    }
}

