package com.train.rabbitmq01api.api.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

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
		
		// 这就是一个普通的交换机 和 队列 以及路由
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "test_dlx_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);

		// 视频3_12-14 死信队列详解
		// 在队列加上一个参数"x-dead-letter-exchange", "dlx.exchange",就是指定死信队列的交换机
		// 这样消息在过期、requeue、 队列在达到最大长度时，消息就可以直接路由到死信队列
		Map<String, Object> agruments = new HashMap<String, Object>();
		agruments.put("x-dead-letter-exchange", "dlx.exchange");
		//这个agruments属性，要设置到声明队列上
		channel.queueDeclare(queueName, true, false, false, agruments);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//要进行死信队列的声明:
		// 死信队列的交换机
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
		// 死信队列的队列
		channel.queueDeclare("dlx.queue", true, false, false, null);
		// 绑定关系,注意routingKey是#表示所有
		channel.queueBind("dlx.queue", "dlx.exchange", "#");
		
		channel.basicConsume(queueName, true, new MyConsumerForDlx(channel));
		
		
	}
}
