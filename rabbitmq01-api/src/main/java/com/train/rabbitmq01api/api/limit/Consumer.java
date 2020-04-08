package com.train.rabbitmq01api.api.limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;

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


		String exchangeName = "test_qos_exchange";
		String queueName = "test_qos_queue";
		String routingKey = "qos.#";

		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		// 视频3_8-9 消费端的限流策略
		// 参数: 1 prefetchSize 消费单条消息的大小限制，消费端通常设置为0，表示不做限制,
		// 		2 prefetchCount 一次最多能处理多少条消息，通常设置为1,
		// 		3 global 是否将上面设置应用于channel，false代表consumer级别
		// prefetchSize和global这两项，rabbitmq没有实现，暂且不研究
		// prefetchCount在 autoAck=false 的情况下生效，即在自动应答的情况下这个值是不生效的
		// 进行参数设置：单条消息的大小限制，一次最多能处理多少条消息，是否将上面设置应用于channel
		channel.basicQos(0, 1, false);

		//5 创建消费者(这个消费者对象设置的手工ACK)
		DefaultConsumer queueingConsumer = new MyConsumerManualAck(channel);
		// 手工签收 必须要关闭 autoAck = false
		channel.basicConsume(queueName, false, queueingConsumer);
		
		
	}
}
