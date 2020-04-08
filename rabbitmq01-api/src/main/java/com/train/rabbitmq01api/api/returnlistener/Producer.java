package com.train.rabbitmq01api.api.returnlistener;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

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
		
		String exchange = "test_return_exchange";
		// 绑定queue的routingKey是return.#
		String routingKey = "return.save";
		// 这个是没有绑定queue的routingKey,毫无疑问发不出去
		String routingKeyError = "abc.save";
		
		String msg = "Hello RabbitMQ Return Message";
		
		// 视频3_6 Return返回消息详解
		// 添加一个ReturnListener
		channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode, String replyText, String exchange,
					String routingKey, BasicProperties properties, byte[] body) throws IOException {
				
				System.err.println("---------handle  return----------");
				System.err.println("replyCode: " + replyCode);
				System.err.println("replyText: " + replyText);
				System.err.println("exchange: " + exchange);
				System.err.println("routingKey: " + routingKey);
				System.err.println("properties: " + properties);
				System.err.println("body: " + new String(body));
			}
		});

		// 视频3_6 Return返回消息详解
		// 参数分别为: 1 exchange   2 routingKey  3 props(信息的参数)  4 body(信息本体)
		// 设置Mandatory：如果为true，则监听器会接收到路由不可达的消息，然后进行后续处理，
		// 如果为false，那么broker端自动删除该消息！
		channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		
		//channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		
		
		
		
	}
}
