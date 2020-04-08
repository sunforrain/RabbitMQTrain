package com.train.rabbitmq01api.api.dlx;

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
		
		String exchange = "test_dlx_exchange";
		String routingKey = "dlx.save";
		
		String msg = "Hello RabbitMQ DLX Message";
		
		for(int i =0; i<1; i ++){
			
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2)
					.contentEncoding("UTF-8")
					.expiration("10000") // 设置消息过期时间方便观察死信队列
					.build();
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}
		
	}
}
