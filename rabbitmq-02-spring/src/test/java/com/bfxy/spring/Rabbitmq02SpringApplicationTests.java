package com.bfxy.spring;

import com.bfxy.spring.entity.Order;
import com.bfxy.spring.entity.Packaged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Rabbitmq02SpringApplicationTests {
    // 视频4_1-2 本章导航及SpringAMQP用户管理组件-RabbitAdmin应用-1,注入
    @Autowired
    private RabbitAdmin rabbitAdmin;
    // 视频4_6 SpringAMQP消息模板组件-RabbitTemplate实战, 注入
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void contextLoads() {
    }

    /**
     * 视频4_1-2 本章导航及SpringAMQP用户管理组件-RabbitAdmin应用-1
     * 建立测试方法
     * 视频4_3 SpringAMQP用户管理组件-RabbitAdmin应用-2
     * 在控制台提前将queue里面的消息清空,purge一下
     * @throws Exception
     */
    @Test
    public void testAdmin() throws Exception {
        // 视频4_3 SpringAMQP用户管理组件-RabbitAdmin应用-2 各种创建exchange,queue和binding关系等
        // ctrl + p可以提示方法内参数
        // 这里new的exchange都是springamqp重新封装的,更易用
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));

        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));

        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        // 已经创建过的代码不会再次被执行,到现在我们建立的exchange和queue都没有任何绑定关系和参数
        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));

        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));

        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        // 绑定关系方式一, 用Binding来建立
        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                                        Binding.DestinationType.QUEUE,
                                        "test.direct",
                                        "direct", new HashMap<>()));

        // 绑定关系方式二, 用BindingBuilder来建,是链式编程, bind => to => with
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.topic.queue", false))		//直接创建队列
                        .to(new TopicExchange("test.topic", false, false))	//直接创建交换机 建立关联关系
                        .with("user.#"));	//指定路由Key

        // fanout类型的exchange没有路由键,没有with
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.fanout.queue", false))
                        .to(new FanoutExchange("test.fanout", false, false)));

        //清空队列数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);
    }

    /**
     * 视频4_6 SpringAMQP消息模板组件-RabbitTemplate实战
     * 有后置处理的消息发送
     * @throws Exception
     */
    @Test
    public void testSendMessage() throws Exception {
        //1 创建消息,需要有消息体和messageProperties
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述..");
        messageProperties.getHeaders().put("type", "自定义消息类型..");
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

        // convertAndSend有很多重载的方法
        rabbitTemplate.convertAndSend("topic001", "spring.amqp",
                                message, new MessagePostProcessor() {
            // xxxPostProcessor是后置处理器的常见命名,这里可以在消息传过来之后对message再进行一些设置,然后再进行发送
            // ctrl+o实现方法
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.err.println("------添加额外的设置---------");
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
                message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
                return message;
            }
        });
    }

    /**
     * 视频4_6 SpringAMQP消息模板组件-RabbitTemplate实战
     * 简单的消息发送
     * @throws Exception
     */
    @Test
    public void testSendMessage2() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.abc", message);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
    }

    // 视频4_8 SpringAMQP消息适配器-MessageListenerAdapter使用-1
    // 用于测试String转换器的测试方法
    @Test
    public void testSendMessage4Text() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }

    // 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1
    @Test
    public void testSendJsonMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }

    // 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1,java对象转换
    @Test
    public void testSendJavaMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        // 转换为java对象还需要指定转换的类全类名,key为__TypeId__
        messageProperties.getHeaders().put("__TypeId__", "com.bfxy.spring.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }

    // 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1,
    // 支持java对象多映射转换
    @Test
    public void testSendMappingMessage() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");

        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        // 因为提前用map设置了key,这里指定类的时候不用全类名了
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);

        Packaged pack = new Packaged();
        pack.setId("002");
        pack.setName("包裹消息");
        pack.setDescription("包裹描述信息");

        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }

    // 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-2, 传入消息为文件
    @Test
    public void testSendExtConverterMessage() throws Exception {
        // 获取图片,转为二进制数据
        byte[] body = Files.readAllBytes(Paths.get("d:/", "微信截图_20200327221030.png"));
        MessageProperties messageProperties = new MessageProperties();
        // 这里设置ContentType和Header的参数对于转换器辨认是什么文件以及用什么转换器很重要
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName", "png");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "image_queue", message);

        // 获取pdf文件
//        byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType("application/pdf");
//        Message message = new Message(body, messageProperties);
//        rabbitTemplate.send("", "pdf_queue", message);
    }
}
