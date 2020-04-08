package com.train.rabbitmq01api.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Procuder {

	/**
	 * 视频2_10 生产者与消费者模型构建-1
	 * 构建生产者,注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//1 创建一个ConnectionFactory, 并进行配置
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.13.128");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 通过连接工厂创建连接
		Connection connection = connectionFactory.newConnection();
		
		//3 通过connection创建一个Channel
		Channel channel = connection.createChannel();
		
		//4 通过Channel发送数据
		for(int i=0; i < 5; i++){
			String msg = "Hello RabbitMQ!";
			//参数分别为: 1 exchange   2 routingKey  3 props(信息的参数)  4 body(信息本体)
			// 注意这里没有指定exchange, 那么rabbitmq会默认指定为 AMQP default,
			// 	这个交换器默认会将routingKey与同名的队列Binding起来
			channel.basicPublish("", "test001", null, msg.getBytes());
		}

		//5 记得要关闭相关的连接
		channel.close();
		connection.close();
	}
}
