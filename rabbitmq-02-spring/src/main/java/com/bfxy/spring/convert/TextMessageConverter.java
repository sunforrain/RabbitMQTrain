package com.bfxy.spring.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * 视频4_8 SpringAMQP消息适配器-MessageListenerAdapter使用-1
 * 专门用于将byte[]转为String的转换器
 * implements MessageConverter是关键
 */
public class TextMessageConverter implements MessageConverter {

	// 将java对象转为message对象
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		return new Message(object.toString().getBytes(), messageProperties);
	}

	// 将传入的消息message转为java对象
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		String contentType = message.getMessageProperties().getContentType();
		// 这里还是判断了一下,contentType为text才转为String
		if(null != contentType && contentType.contains("text")) {
			return new String(message.getBody());
		}
		return message.getBody();
	}

}
