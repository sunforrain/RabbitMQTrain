package com.bfxy.springboot.conusmer;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

// 视频4_14 RabbitMQ与SpringBoot2.0整合实战-3, 添加消费者的工具类
@Component
public class RabbitReceiver {

	/**
	 * 视频4_14 RabbitMQ与SpringBoot2.0整合实战-3,
	 * @RabbitListener 的演示,这个注解不仅可以处理绑定关系,还能进行创建等,很方便
	 * @param message
	 * @param channel
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "queue-1", 
			durable="true"),
			exchange = @Exchange(value = "exchange-1", 
			durable="true", 
			type= "topic", 
			ignoreDeclarationExceptions = "true"),
			key = "springboot.*"
			)
	)
	@RabbitHandler // @RabbitHandler需要和@RabbitListener配合使用
	public void onMessage(Message message, Channel channel) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端Payload: " + message.getPayload());
		Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK,不批量
		channel.basicAck(deliveryTag, false);
	}
	
	
	/**
	 * 视频4_15 RabbitMQ与SpringBoot2.0整合实战-4,
	 * 传配置文件参数,用spel的方式给@RabbitListener
	 * @Payload 标记在方法形参上,标记入参为消息体,可以指定传入的消息为java对象
	 * @Headers 标记在方法形参上,标记入参为header
	 * @param order
	 * @param channel
	 * @param headers
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}", 
			durable="${spring.rabbitmq.listener.order.queue.durable}"),
			exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}", 
			durable="${spring.rabbitmq.listener.order.exchange.durable}", 
			type= "${spring.rabbitmq.listener.order.exchange.type}", 
			ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
			key = "${spring.rabbitmq.listener.order.key}"
			)
	)
	@RabbitHandler
	public void onOrderMessage(@Payload com.bfxy.springboot.entity.Order order, 
			Channel channel, 
			@Headers Map<String, Object> headers) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端order: " + order.getId());
		Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK
		channel.basicAck(deliveryTag, false);
	}
	
	
}
