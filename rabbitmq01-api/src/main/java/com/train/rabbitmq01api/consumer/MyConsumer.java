package com.train.rabbitmq01api.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * 视频2_10 生产者与消费者模型构建-2
 * 视频3_7 自定义消费者使用
 * 定义一个消费者,springBoot2之后换方法了, QueueingConsumer不用了
 */
public class MyConsumer extends DefaultConsumer {

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MyConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //consumerTag: 内部生成的消费标签  properties: 消息属性  body: 消息内容
        System.err.println("-----------consume message----------");
        System.err.println("consumerTag: " + consumerTag);
        //envelope包含属性：deliveryTag(标签), redeliver, exchange, routingKey
        //redeliver是一个标记，如果设为true，表示消息之前可能已经投递过了，现在是重新投递消息到监听队列的消费者
        System.err.println("envelope: " + envelope);
        System.err.println("properties: " + properties);
        System.err.println("body: " + new String(body));
    }
}
