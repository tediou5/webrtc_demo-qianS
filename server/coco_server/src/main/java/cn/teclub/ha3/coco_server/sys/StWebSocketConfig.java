package cn.teclub.ha3.coco_server.sys;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class StWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        System.out.println("==== cook: enable simple broker");
        registry.enableSimpleBroker("/topic", "/b", "/g", "/u");
        registry.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("==== cook: register end-point");
        registry.addEndpoint("/ep-st-websocket").withSockJS();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024 * 512);
        container.setMaxBinaryMessageBufferSize(1024 * 512);
        return container;
    }
    /*
     * [Theodor: 2019/1/24]
     *
   	// 配置使用消息代理
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 统一配置消息代理，消息代理即订阅点，客户端通过订阅消息代理点接受消息
		registry.enableSimpleBroker("/b", "/g", "/user");

		// 配置点对点消息的前缀
		registry.setUserDestinationPrefix("/user");
	}
     */

}


