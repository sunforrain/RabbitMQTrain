package com.train.rabbitmq01api.api.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer4DirectExchange {

	/**
	 * 视频2_12 交换机讲解-1
	 * direct类型的交换机,特点是消息直接被转发到RouteKey 一致的 Queue,这个规则和自带的AMQP default是一样的
	 * 注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.13.128");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 创建Connection
		Connection connection = connectionFactory.newConnection();
		//3 创建Channel
		Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_direct_exchange";
		String routingKey = "test.direct";
		//5 发送
		
		String msg = "Hello World RabbitMQ 4  Direct Exchange Message 111 ... ";
		channel.basicPublish(exchangeName, routingKey , null , msg.getBytes()); 		
		
	}
	
}
