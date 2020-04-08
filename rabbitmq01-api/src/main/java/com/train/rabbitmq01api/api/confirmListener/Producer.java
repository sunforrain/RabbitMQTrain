package com.train.rabbitmq01api.api.confirmListener;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	/**
	 * 构建生产者,注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.13.128");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		//2 获取C	onnection
		Connection connection = connectionFactory.newConnection();
		
		//3 通过Connection创建一个新的Channel
		Channel channel = connection.createChannel();
		
		// 视频3_5 RabbitMQ确认消息Confirm详解
		//4 指定我们的消息投递模式: 消息的确认模式 
		channel.confirmSelect();
		
		String exchangeName = "test_confirm_exchange";
		String routingKey = "confirm.save";
		// 这里如磁盘写满了，MQ出现了一些异常，或者Queue容量到达上限了等等,就能看到handleNack

		//5 发送一条消息
		String msg = "Hello RabbitMQ Send confirm message!";
		//参数分别为: 1 exchange   2 routingKey  3 props(信息的参数)  4 body(信息本体)
		channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());

		// 视频3_5 RabbitMQ确认消息Confirm详解
		//6 添加一个确认监听ConfirmListener
		channel.addConfirmListener(new ConfirmListener() {
			// 消息失败处理
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------no ack!-----------");
			}

			// 消息成功处理
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------ack!-----------");
			}
		});
		
		
		
		
		
	}
}
