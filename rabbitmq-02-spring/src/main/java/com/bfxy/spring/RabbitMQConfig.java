package com.bfxy.spring;

import com.bfxy.spring.adapter.MessageDelegate;
import com.bfxy.spring.convert.ImageMessageConverter;
import com.bfxy.spring.convert.PDFMessageConverter;
import com.bfxy.spring.convert.TextMessageConverter;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 视频4_1-2 本章导航及SpringAMQP用户管理组件-RabbitAdmin应用-1
 * 增加rabbitMq的配置类和自动扫描的包范围
 */
@Configuration
@ComponentScan({"com.bfxy.spring"})
public class RabbitMQConfig {

    /**
     * 视频4_1-2 本章导航及SpringAMQP用户管理组件-RabbitAdmin应用-1
     * 设置一个ConnectionFactory注入到spring容器中
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("192.168.13.128:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    /**
     * 视频4_1-2 本章导航及SpringAMQP用户管理组件-RabbitAdmin应用-1
     * 创建一个rabbitAdmin
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        // 创建一个rabbitAdmin,后面围绕它使用
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 设置自动启动,让IOC容器初始化的同时rabbitAdmin也要启动
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 视频4_4-5 RabbitAdmin源码分析及RabbitMQ声明式配置使用
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }
    // 注意这里,两个queue被绑定到一个exchange上了,但是routingKey不同
    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }

    /**
     * 视频4_6 SpringAMQP消息模板组件-RabbitTemplate实战
     * @param connectionFactory 传入的是注入到容器的connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {

        // 视频4_7 SpringAMQP消息容器-SimpleMessageListenerContainer详解,创建SimpleMessageListenerContainer
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 设置要监控的队列
        container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        // 设置当前消费者数量
        container.setConcurrentConsumers(1);
        // 设置最大消费者数量
        container.setMaxConcurrentConsumers(5);
        // 是否设置重回队列
        container.setDefaultRequeueRejected(false);
        // 设置签收模式,这里是自动签收
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        // 设置监听器是否外露
        container.setExposeListenerChannel(true);
        // 设置消费端的标签策略,看管控台内看到内容
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            // 可以定制tag的内容
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });

        /*
        // 设置一个监听器,消息过来后可以看到onMessage会调用
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                // getBody获取消息实体,还可以getMessageProperties
                String msg = new String(message.getBody());
                System.err.println("----------消费者: " + msg);
            }
        });
        // 其他container的设置也常见的还有加后置处理方法等等
        return container;
         */


        /*
            视频4_8 SpringAMQP消息适配器-MessageListenerAdapter使用-1
            屏掉上面监听相关的代码,改为适配器的方式
            简单来说通过Adapter定制自己的监听
            1 适配器方式. 默认是有自己的方法名字的：handleMessage
            可以自己指定一个方法的名字: consumeMessage/consumeMessageToString
            也可以添加一个转换器: 从字节数组转换为String
        */
        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        // 设置MessageListenerAdapter监听消息的方法为自定义的
        adapter.setDefaultListenerMethod("consumeMessageToString");
        // 如果需要将传入的消息转为byte[]之外的类型,需要注册对应的Converter
        adapter.setMessageConverter(new TextMessageConverter());
        container.setMessageListener(adapter);
        return container;
        */

        /*
         * 视频4_9 SpringAMQP消息适配器-MessageListenerAdapter使用-2
         * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
         *    实现对不同队列有不同的message监听方法
        */
        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setMessageConverter(new TextMessageConverter());
        Map<String, String> queueOrTagToMethodName = new HashMap<>();
        queueOrTagToMethodName.put("queue001", "method1");
        queueOrTagToMethodName.put("queue002", "method2");
        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
        container.setMessageListener(adapter);
        return container;
        */

        /**
         * 视频4_10 SpringAMQP消息转换器-MessageConverter讲解-1
         * 1.1 支持json格式的转换器
         */
        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessageToMap");

        // 核心的区别就是在adapter上加了jackson2JsonMessageConverter,这个可以将json转为map
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        adapter.setMessageConverter(jackson2JsonMessageConverter);

        container.setMessageListener(adapter);
        return container;
        */


        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        // 这里是把DefaultJackson2JavaTypeMapper放到jackson2JsonMessageConverter里面,一个嵌套关系
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        // 设置信任列表,否则报错The class 'com.bfxy.spring.entity.Order' is not in the trusted packages
        javaTypeMapper.setTrustedPackages("*");
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        return container;
        */


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
        // 这里会根据consumeMessage的不同方法参数去找对应类的监听方法
        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        javaTypeMapper.setTrustedPackages("*");

        // 主要就是给DefaultJackson2JavaTypeMapper设置可转换java对象的map, setIdClassMapping
        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
        idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
        idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);

        javaTypeMapper.setIdClassMapping(idClassMapping);

        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        return container;
        */

        // 视频4_11 SpringAMQP消息转换器-MessageConverter讲解-2
        // 1.4 ext convert,ContentTypeDelegatingMessageConverter支持设置多个转换器以及每个转换器对应的转换类型
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局的转换器:
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        javaTypeMapper.setTrustedPackages("*");
        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
        idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
        idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMapping);
        jsonConvert.setJavaTypeMapper(javaTypeMapper);
        convert.addDelegate("json", jsonConvert);
        convert.addDelegate("application/json", jsonConvert);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        convert.addDelegate("image/png", imageConverter);
        convert.addDelegate("image", imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        convert.addDelegate("application/pdf", pdfConverter);


        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);
        return container;


    }
}
