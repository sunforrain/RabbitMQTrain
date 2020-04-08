package com.train.rabbitmq01api.api.limit;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	/**
	 * 构建生产者,注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
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

		String exchange = "test_qos_exchange";
		String routingKey = "qos.save";

		String msg = "Hello RabbitMQ QOS Message";

		for(int i =0; i<5; i ++){
			channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
		}

		
	}
}
