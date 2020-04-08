package com.bfxy.springboot;

import com.bfxy.springboot.entity.Order;
import com.bfxy.springboot.producer.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rabbitmq03SpringbootProducerApplicationTests {

    @Autowired
    private RabbitSender rabbitSender;

    // SimpleDateFormat注意有线程安全问题
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // 视频4_13 RabbitMQ与SpringBoot2.0整合实战-2, 测试消息的发送,还没见相关的exchange和queue,会去return方法的异常处理
    @Test
    public void testSender1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("send_time", simpleDateFormat.format(new Date()));
        rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
    }

    @Test
    public void testSender2() throws Exception {
        Order order = new Order("001", "第一个订单");
        rabbitSender.sendOrder(order);
    }

}
