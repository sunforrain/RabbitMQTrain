package com.bfxy.spring.convert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

// 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1
// 视频4_11 SpringAMQP消息转换器-MessageConverter讲解-2, 传入消息为文件
public class ImageMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException(" convert error ! ");
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		System.err.println("-----------Image MessageConverter----------");
		// 消息的head里面要有extName这个参数来分辨文件类型
		Object _extName = message.getMessageProperties().getHeaders().get("extName");
		String extName = _extName == null ? "png" : _extName.toString();
		
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
		String path = "d:/" + fileName + "." + extName;
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

}
