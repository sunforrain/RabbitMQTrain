package com.train.rabbitmq01api.api.message;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Procuder {

	/**
	 * 视频2_15-16 绑定,队列,消息,虚拟主机详解及小结
	 * 其他的见视频,这里主要是消息message相关
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
		
		Map<String, Object> headers = new HashMap<>();
		headers.put("my1", "111");
		headers.put("my2", "222");
		
		/*
			设置一些message的常用属性,可见是个链式编程
			deliveryMode: 送达模式, 2是持久化
			contentEncoding: 字符集
			expiration: 过期时间
			headers: 自定义的其他属性,可以同样方式获取
			还有
			contentType:
			priority: 优先级0~9, 9最高,但是集群中并不是优先级最高的能做到最优先处理,需要涉及顺序消息的概念
			correlationId: 辨识消息唯一性的id
			replyTo: 指定如果消息失败了可以返回哪个队列
			messageId: 消息的id
		 */
		AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
				.deliveryMode(2)
				.contentEncoding("UTF-8")
				.expiration("10000")// 视频3_11 TTL消息详解, 注意这里是针对消息设置消息的过期时间为10s
				.headers(headers)
				.build();
		
		//4 通过Channel发送数据
		for(int i=0; i < 5; i++){
			String msg = "Hello RabbitMQ!";
			//参数分别为: 1 exchange   2 routingKey  3 props(信息的参数)  4 body(信息本体)
			channel.basicPublish("", "test001", properties, msg.getBytes());
		}

		//5 记得要关闭相关的连接
		channel.close();
		connection.close();
	}
}
