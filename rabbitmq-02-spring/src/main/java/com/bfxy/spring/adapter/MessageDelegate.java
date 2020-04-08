package com.bfxy.spring.adapter;

import com.bfxy.spring.entity.Order;
import com.bfxy.spring.entity.Packaged;

import java.io.File;
import java.util.Map;

/**
 * 视频4_8 SpringAMQP消息适配器-MessageListenerAdapter使用-1
 * 自定义的处理消息的方法,可以看到下面对于消息的处理方法都是自定义的,也没有什么接口继承
 */
public class MessageDelegate {

	// handleMessage是MessageListenerAdapter默认指定消息体处理方法的名字,但是可以自定义,后面说
	public void handleMessage(byte[] messageBody) {
		System.err.println("默认方法, 消息内容:" + new String(messageBody));
	}

	// 自定义处理消息的方法,注意需要adapter.setDefaultListenerMethod("consumeMessage");指定
	public void consumeMessage(byte[] messageBody) {
		System.err.println("字节数组方法, 消息内容:" + new String(messageBody));
	}

	// 入参不是byte数组,直接变为String,这种处理需要转换器converter
	public void consumeMessageToString(String messageBody) {
		System.err.println("字符串方法, 消息内容:" + messageBody);
	}

	// 视频4_9 SpringAMQP消息适配器-MessageListenerAdapter使用-2
	// 给不同队列指定不同的消息处理方法
	public void method1(String messageBody) {
		System.err.println("method1 收到消息内容:" + new String(messageBody));
	}

	// 视频4_9 SpringAMQP消息适配器-MessageListenerAdapter使用-2
	// 给不同队列指定不同的消息处理方法
	public void method2(String messageBody) {
		System.err.println("method2 收到消息内容:" + new String(messageBody));
	}
	
	// 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1,json转换
	// 如果这个方法也叫consumeMessage,因为方法参数不一样,也能成功识别
	public void consumeMessageToMap(Map messageBody) {
		System.err.println("map方法, 消息内容:" + messageBody);
	}

	// 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1,java对象转换,可以看到重写的效果
	public void consumeMessage(Order order) {
		System.err.println("order对象, 消息内容, id: " + order.getId() +
				", name: " + order.getName() +
				", content: "+ order.getContent());
	}

	// 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1,支持java对象多映射转换
	public void consumeMessage(Packaged pack) {
		System.err.println("package对象, 消息内容, id: " + pack.getId() +
				", name: " + pack.getName() +
				", content: "+ pack.getDescription());
	}

	// 视频4_11 SpringAMQP消息转换器-MessageConverter讲解-2, 传入文件的转换器
	public void consumeMessage(File file) {
		System.err.println("文件对象 方法, 消息内容:" + file.getName());
	}
}
