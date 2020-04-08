package com.train.rabbitmq01api.api.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.train.rabbitmq01api.consumer.MyConsumer;

public class Consumer4TopicExchange {

	/**
	 * 视频2_12 交换机讲解-2
	 * topic类型的交换机,特点是exchange将routingKey和某topic进行模糊匹配
	 * 注意先运行消费者再运行消费者,因为消费者的逻辑有生成队列
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
        ConnectionFactory connectionFactory = new ConnectionFactory() ;

        connectionFactory.setHost("192.168.13.128");
        connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();  
		//4 声明
		String exchangeName = "test_topic_exchange";
		String exchangeType = "topic";
		String queueName = "test_topic_queue";
		// # 表示匹配一个或多个词,如user.#可以匹配user.lead.oo
		// * 表示只能匹配一个
		//String routingKey = "user.*";
		String routingKey = "user.*";
		// 1 声明交换机 
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		// 2 声明队列
		channel.queueDeclare(queueName, false, false, false, null);
		// 3 建立交换机和队列的绑定关系:
		channel.queueBind(queueName, exchangeName, routingKey);
		
        //durable 是否持久化消息
        DefaultConsumer queueingConsumer = new MyConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, queueingConsumer);

        // 7 获取消息 放到MyConsumer的handleDelivery实现方法内
        //循环获取消息  
//        while(true){
//            //获取消息，如果没有消息，这一步将会一直阻塞
//            Delivery delivery = consumer.nextDelivery();
//            String msg = new String(delivery.getBody());
//            System.out.println("收到消息：" + msg);
//        }
	}
}
