package cn.happyselect.sys.config.redis;

import cn.happyselect.sys.config.security.CustomSecurityMetadataSource;
import cn.happyselect.sys.config.security.json.CustomJackson2Module;
import cn.happyselect.sys.config.websocket.WebSocketMessageConsumer;
import cn.happyselect.sys.constant.RedisChannelTopicConstant;
import cn.happyselect.sys.service.PrivilegeSyncProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.concurrent.Executor;

/**
 * redis配置
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-24
 */
@Configuration
public class RedisSessionConfig {

    public static final String HEADER_X_AUTH_TOKEN = "x-auth-token";

    @Bean
    public HttpSessionIdResolver httpSessionStrategy() {
        MultiHttpSessionIdResolver multiHttpSessionIdResolver = new MultiHttpSessionIdResolver();
        multiHttpSessionIdResolver.add(new HeaderHttpSessionIdResolver(HEADER_X_AUTH_TOKEN));
        multiHttpSessionIdResolver.add(new CookieHttpSessionIdResolver());
        return multiHttpSessionIdResolver;
    }

    /**
     * 自定义spring session序列化工具
     *
     * @return
     */
    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer springSessionDefaultRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        mapper.registerModules(new CustomJackson2Module());
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    /**
     * 自定义sessionRedis线程池
     *
     * @return
     */
    @Bean("springSessionRedisTaskExecutor")
    public Executor springSessionRedisTaskExecutor() {
        ThreadPoolTaskExecutor springSessionRedisTaskExecutor = new ThreadPoolTaskExecutor();
        springSessionRedisTaskExecutor.setCorePoolSize(8);
        springSessionRedisTaskExecutor.setMaxPoolSize(16);
        springSessionRedisTaskExecutor.setKeepAliveSeconds(10);
        springSessionRedisTaskExecutor.setQueueCapacity(1000);
        springSessionRedisTaskExecutor.setThreadNamePrefix("Spring session redis executor thread: ");
        return springSessionRedisTaskExecutor;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 设置广播监听的通道
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   CustomSecurityMetadataSource customSecurityMetadataSource,
                                                   WebSocketMessageConsumer webSocketMessageConsumer) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(customSecurityMetadataSource, new ChannelTopic(PrivilegeSyncProducer.PRIVILEGE_SYNC));
        container.addMessageListener(webSocketMessageConsumer, new ChannelTopic(RedisChannelTopicConstant.WEBSOCKET_TOPIC));
        container.setTaskExecutor(springSessionRedisTaskExecutor());
        return container;
    }

}
