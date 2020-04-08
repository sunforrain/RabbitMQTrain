package com.train.rabbitmq01api.api.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.train.rabbitmq01api.consumer.MyConsumer;

public class Consumer {

	/**
	 * 构建消费者,注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.13.128");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		
		String exchangeName = "test_consumer_exchange";
		String routingKey = "consumer.#";
		String queueName = "test_consumer_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		// 视频3_7 自定义消费者使用
		channel.basicConsume(queueName, true, new MyConsumer(channel));
		
		
	}
}
