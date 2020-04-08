package com.train.rabbitmq01api.api.limit;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyConsumerManualAck extends DefaultConsumer {


	private Channel channel ;

	public MyConsumerManualAck(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));

		// 视频3_8-9 消费端的限流策略
		// 手工ACK，参数multiple表示不批量签收
		// 注释掉手工ACK方法，然后启动消费端和生产端，此时消费端只打印了一条消息,放开则逐条确认
//		channel.basicAck(envelope.getDeliveryTag(), false);

	}


}
