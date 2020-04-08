package com.train.rabbitmq01api.api.returnlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
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
		
		String exchangeName = "test_return_exchange";
		String routingKey = "return.#";
		String queueName = "test_return_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		//5 创建消费者(需要创建一个DefaultConsumer的子类)
		DefaultConsumer queueingConsumer = new MyConsumer(channel);

		//6 设置Channel
		//参数 1 queue 要消费的队列  2 autoAck 是否自动签收,true的话消费端会回送给mq一个ack消息  3 callback  具体的消费者对象
		channel.basicConsume(queueName, true, queueingConsumer);

		//7 获取消息 放到MyConsumer的handleDelivery实现方法内
//		while(true){
//			Delivery delivery = queueingConsumer.nextDelivery();
//			String msg = new String(delivery.getBody());
//
//			System.err.println("消费端: " + msg);
//		}
		
	}
}
