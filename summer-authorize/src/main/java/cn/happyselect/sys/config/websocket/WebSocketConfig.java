package cn.happyselect.sys.config.websocket;

import cn.happyselect.sys.constant.DestinationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.handler.WebSocketConnectHandlerDecoratorFactory;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * WebSocketConfig
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-22
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig<S extends Session> extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Bean
    public WebSocketConnectHandlerDecoratorFactory wsConnectHandlerDecoratorFactory() {
        return new WebSocketConnectHandlerDecoratorFactory(this.eventPublisher);
    }

    @Bean
    public SessionRepositoryMessageInterceptor<S> sessionRepositoryInterceptor() {
        return new SessionRepositoryMessageInterceptor<>(this.sessionRepository);
    }

    @Bean
    public SubscribeInterceptor subscribeInterceptor() {
        return new SubscribeInterceptor();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .addInterceptors(sessionRepositoryInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix(DestinationConstant.USER_PREFIX);
        registry.enableSimpleBroker(DestinationConstant.TOPIC_PREFIX, DestinationConstant.QUEUE_PREFIX);
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher().authenticated()
                .simpDestMatchers("/user/**").authenticated();
    }

    @Override
    public void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionRepositoryInterceptor());
        registration.interceptors(subscribeInterceptor());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(wsConnectHandlerDecoratorFactory());
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
