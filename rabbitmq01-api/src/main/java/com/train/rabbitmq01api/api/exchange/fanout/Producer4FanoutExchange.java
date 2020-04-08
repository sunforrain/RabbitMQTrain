package com.train.rabbitmq01api.api.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer4FanoutExchange {

	/**
	 * 视频2_12 交换机讲解-3
	 * fanout类型的交换机,特点是不处理routingKey,直接发送消息到绑定的所有queue上,转发消息的速度也是最快的
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
		String exchangeName = "test_fanout_exchange";
		//5 发送
		for(int i = 0; i < 10; i ++) {
			String msg = "Hello World RabbitMQ 4 FANOUT Exchange Message ...";
			channel.basicPublish(exchangeName, "", null , msg.getBytes()); 			
		}
		channel.close();  
        connection.close();  
	}
	
}
