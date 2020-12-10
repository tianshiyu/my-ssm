package org.dishi.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

@Configuration
@EnableRabbit
@PropertySource("classpath:/rabbitMQ.properties")
public class RabbitConfig {
    @Value("${rabbitmq.host}")
    String host;
    @Value("${rabbitmq.port}")
    Integer port;
    @Value("${rabbitmq.username}")
    String username;
    @Value("${rabbitmq.password}")
    String password;
    @Value("${rabbitmq.listener.class}")
    String listenerClass;
    @Value("${rabbitmq.queuename}")
    String queueName;
    @Value("${rabbitmq.exchange}")
    String exchangeName;
    @Value("${rabbitmq.routingKey}")
    String routingKey;

    @Bean
    MessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
//    @Bean
////    <!-- queue litener  观察 监听模式 当有消息到达时会通知监听在对应的队列上的监听对象-->
////    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, ChannelAwareMessageListener channelAwareMessageListener){
//    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
//
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        //container.setMessageListener(messageListener);
//        container.setChannelAwareMessageListener(channelAwareMessageListener);
//        Queue[] queues=new Queue[queueName.split(",").length];
//        for (int i = 0; i < queues.length; i++) {
//            Queue queue=new Queue(queueName);
//            queues[i]=queue;
//        }
//        container.setQueues(queues);
//        container.setConsumerArguments(Collections. <String, Object> singletonMap("x-priority",
//                Integer.valueOf(10)));
//        return container;
//    }
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(
                host,
                port
        );
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(@Autowired ConnectionFactory connectionFactory,
                                                                               @Autowired MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }


    @Bean
    public RabbitAdmin rabbitAdmin(@Autowired ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    //AmqpTemplate配置，AmqpTemplate接口定义了发送和接收消息的基本操作
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        rabbitTemplate.setMessageConverter(createMessageConverter());
        rabbitTemplate.setRetryTemplate(retryTemplate);
        return rabbitTemplate;
    }
//    @Bean
//    public ChannelAwareMessageListener channelAwareMessageListener() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//        return  (ChannelAwareMessageListener) Class.forName(listenerClass).newInstance();
//    }

    @Bean
    Queue mailQueue() {
        //生成queue
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange mailExchange() {
        //生成exchange
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    Binding mailBinding() {
        //设置queue与exchange关系即routing_key
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(routingKey);
    }
}
