package com.train.rabbitmq01api.api.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.train.rabbitmq01api.consumer.MyConsumer;

public class Consumer4FanoutExchange {

	/**
	 * 视频2_12 交换机讲解-3
	 * fanout类型的交换机,特点是不处理routingKey,直接发送消息到绑定的所有queue上,转发消息的速度也是最快的
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
		String exchangeName = "test_fanout_exchange";
		String exchangeType = "fanout";
		String queueName = "test_fanout_queue";
		String routingKey = "";	//不设置路由键
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		channel.queueDeclare(queueName, false, false, false, null);
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
