package com.example.demo;

import com.example.demo.sub.RequestJt808Subscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * redis数据监听配置类
 */
@Configuration
public class RedisMessageSubConfig {

    @Value("${redis.key.queue.request.jt808}")
    private String redisKeyRequestJt808;


    /**
     * 创建连接工厂
     *
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //接受消息的key
        container.addMessageListener(listenerAdapter, new ChannelTopic(redisKeyRequestJt808));
//        container.addMessageListener(listenerAdapter, new PatternTopic("phone"));

        return container;
    }


    /**
     * 绑定消息监听者和接收监听的方法
     *
     * @return
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RequestJt808Subscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * @return
     */
    @Bean
    public RequestJt808Subscriber receiver() {
        return new RequestJt808Subscriber();
    }
}
