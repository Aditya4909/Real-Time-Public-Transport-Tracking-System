package com.transittrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration enabling STOMP (Simple Text Oriented Messaging Protocol) message broker.
 *
 * Annotations explanation:
 * - @Configuration: Identifies this as a Spring IoC configuration bean.
 * - @EnableWebSocketMessageBroker: Enables WebSocket message handling, backed by a message broker.
 * - WebSocketMessageBrokerConfigurer: Interface providing callback methods to configure WebSocket connection endpoints
 *   and message routing destinations.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker routing options:
     * - In-memory broker with prefix '/topic' carries messages to connected clients subscribed to topics.
     * - Application destination prefix '/app' filters messages routed to @MessageMapping controller handlers.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable an in-memory message broker to carry messages back to clients on destinations prefixed with /topic
        registry.enableSimpleBroker("/topic");

        // Designate the /app prefix for messages that are bound for @MessageMapping-annotated methods
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP handshake endpoint mapping '/ws-transit' with SockJS fallback support
     * so that alternative transport options (HTTP streaming, long-polling) can be used if WebSockets are unavailable.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-transit")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
