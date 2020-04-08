package com.bfxy.springboot.producer;

import com.bfxy.springboot.entity.Order;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

// 生产者用来发送消息的工具类
@Component
public class RabbitSender {

	//自动注入RabbitTemplate模板类,springBoot是已经注册过RabbitTemplate的
	@Autowired
	private RabbitTemplate rabbitTemplate;

	// 视频4_12 RabbitMQ与SpringBoot2.0整合实战-1
	//发送消息方法调用: 构建Message消息
	public void send(Object message, Map<String, Object> properties) throws Exception {
		MessageHeaders mhs = new MessageHeaders(properties);
		Message msg = MessageBuilder.createMessage(message, mhs);
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		//id + 时间戳 全局唯一, 这个是用来辨认消息的id!!
		CorrelationData correlationData = new CorrelationData("1234567890");
		rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
	}

	// 视频4_13 RabbitMQ与SpringBoot2.0整合实战-2
	// 回调函数: confirm确认, 注意用spring重新封装的包
	final ConfirmCallback confirmCallback = new ConfirmCallback() {
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			System.err.println("correlationData: " + correlationData);
			System.err.println("ack: " + ack);
			// 这里是直接投递失败,会有失败原因,
			System.err.println("cause: " + cause);
			if(!ack){
				System.err.println("异常处理....");
			}
		}
	};

	// 视频4_13 RabbitMQ与SpringBoot2.0整合实战-2
	// 回调函数: return返回, 注意用spring重新封装的包
	// 注意return和异常是不同的,return是消息成功投递到broker了,但路由键等缺失导致消息发送不能路由又送回来才有的情况
	final ReturnCallback returnCallback = new ReturnCallback() {
		@Override
		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
				String exchange, String routingKey) {
			System.err.println("return exchange: " + exchange + ", routingKey: " 
				+ routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
			System.err.println("return message: " + message.toString());
		}
	};


	// 视频4_15 RabbitMQ与SpringBoot2.0整合实战-4,发送一个java对象的消息
	//发送消息方法调用: 构建自定义对象消息
	public void sendOrder(Order order) throws Exception {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		//id + 时间戳 全局唯一
		CorrelationData correlationData = new CorrelationData("0987654321");
		rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, correlationData);
	}
	
}
