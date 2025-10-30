package com.example.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    

    /**
     * @Configuration: Marks this class as a Spring configuration class, meaning it contains bean definitions and configuration settings
     *  @EnableWebSocketMessageBroker: Enables WebSocket message handling with a message broker, allowing for real-time bidirectional communication
     *  @WebSocketMessageBrokerConfigurer: Interface that provides methods to configure WebSocket message broker settings
     * enableSimpleBroker("/topic", "/queue"):

    Sets up an in-memory message broker
    /topic - typically used for broadcasting messages to multiple subscribers (like chat rooms)
    /queue - typically used for point-to-point messaging
    setApplicationDestinationPrefixes("/app"):

    Messages sent to destinations starting with /app will be routed to @MessageMapping annotated methods in your controllers
    For example, a message to /app/chat would be handled by a method with @MessageMapping("/chat")
    setUserDestinationPrefix("/user"):

    Enables private messaging to specific users
    Messages sent to /user/{username}/queue/... will be delivered only to that specific user
     */


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages back to the client
        // on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Define prefix for messages that are bound for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Optional: Set user destination prefix for private messages
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {


        /**
         * 
         * addEndpoint("/ws"): Creates a WebSocket endpoint at /ws where clients can connect

setAllowedOrigins("http://localhost:5173"):

Configures CORS to allow connections from http://localhost:5173 (likely your frontend development server)
This is important for security and preventing unauthorized cross-origin requests
setAllowedOriginPatterns("*"):

Allows connections from any origin (wildcard pattern)
Note: This is less secure and should be configured more restrictively in production
withSockJS():

Enables SockJS fallback options
SockJS provides WebSocket-like functionality even when WebSocket is not available (older browsers or restrictive networks)
         */
        // Register STOMP endpoint for WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // Fixed: removed /ws from origin
                .setAllowedOriginPatterns("*") // Configure CORS as needed
                .withSockJS(); // Enable SockJS fallback options
    }
}
