package com.bfxy.spring.convert;

// 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1
public class ConverterBody {

	private byte[] body;
	
	public ConverterBody() {
	}

	public ConverterBody(byte[] body) {
		this.body = body;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
