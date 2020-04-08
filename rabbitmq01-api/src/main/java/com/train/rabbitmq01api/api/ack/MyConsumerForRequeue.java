package com.train.rabbitmq01api.api.ack;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyConsumerForRequeue extends DefaultConsumer {


	private Channel channel ;
	
	public MyConsumerForRequeue(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("body: " + new String(body));
		// 3_10 消费端ACK与重回队列机制
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if((Integer)properties.getHeaders().get("num") == 0) {
			// 3_10 消费端ACK与重回队列机制
			// 这里让序号为0的消息unAck
			// 1 deliveryTag 消息标签,
			// 2 multiple 是否是批量的,
			// 3 requeue 是否重回队列, 设置为true
			channel.basicNack(envelope.getDeliveryTag(), false, true);
		} else {
			channel.basicAck(envelope.getDeliveryTag(), false);
		}
		
	}


}
