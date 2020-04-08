package com.train.rabbitmq01api.quickstart;

import com.rabbitmq.client.*;
import com.train.rabbitmq01api.consumer.MyConsumer;

public class Consumer {

	/**
	 * 视频2_10 生产者与消费者模型构建-2
	 * 构建消费者,注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
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
		
		//3 通过connection创建一个Channel,这是核心部分,没有它什么都做不了
		Channel channel = connection.createChannel();
		
		//4 声明（创建）一个队列
		String queueName = "test001";
		// 参数: 1 queue 队列名称   2 durable 是否持久化  3 exclusive 是否独占,就像加把锁,让消息可以顺序被消费
		// 		4  autoDelete 是否自动删除  5  arguments 附加参数
		channel.queueDeclare(queueName, true, false, false, null);
		
		//5 创建消费者(需要创建一个DefaultConsumer的子类)
		DefaultConsumer queueingConsumer = new MyConsumer(channel);
		
		//6 设置Channel
		// 参数 1 queue 要消费的队列  2 autoAck 是否自动签收,true的话消费端会回送给mq一个ack消息  3 callback  具体的消费者对象
		channel.basicConsume(queueName, true, queueingConsumer);

		// 7 获取消息 放到MyConsumer的handleDelivery实现方法内

//		while(true){
//			//7 获取消息
//			Delivery delivery = channel.getDefaultConsumer().
//			String msg = new String(delivery.getBody());
//			System.err.println("消费端: " + msg);
//			//Envelope envelope = delivery.getEnvelope();
//		}
		
	}
}
