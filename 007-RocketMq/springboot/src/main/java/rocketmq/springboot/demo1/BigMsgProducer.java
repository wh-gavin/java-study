package rocketmq.springboot.demo1;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 发送大于4MB的消息
 * @author TAO
 * @date 2021/12/9 10:45 下午
 */

public class BigMsgProducer {

    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("172.17.11.35:9876");
        //3.启动producer
        producer.start();

        List<Message> messages = new ArrayList<Message>();

        int i = 0;
        while (i<10000){
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            String context = "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳" +
                    "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳";
            Message msg = new Message("BatchTopic", "Tag1", (context+ 1).getBytes());
            messages.add(msg);
            i++;
        }
        System.out.println("此次发送批量消息条数==>"+messages.size());
        int frequency = 0;
        //把大的消息分裂成若干个小的消息
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            ++frequency;
            System.out.println("第"+frequency+"次");
            try {
                List<Message>  listItem = splitter.next();
                //5.发送消息
                SendResult result =producer.send(listItem);
                //发送状态
                SendStatus status = result.getSendStatus();
                //System.out.println("发送结果:" + result);
            } catch (Exception e) {
                e.printStackTrace();
                //处理error
            }
        }
        //6.关闭生产者producer
        producer.shutdown();
    }
}
