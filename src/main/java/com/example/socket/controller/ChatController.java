package com.example.socket.controller;



import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.socket.model.ChatMessage;

@Controller
public class ChatController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        // Here you can add logic to process the incoming message if needed

        /**
         * 
         * @MessageMapping("/chat"):

Maps incoming WebSocket messages sent to /app/chat (remember the /app prefix from WebSocketConfig)
When a client sends a message to /app/chat, this method will handle it
@SendTo("/topic/messages"):

Automatically sends the return value of this method to all clients subscribed to /topic/messages
This creates a broadcast mechanism - one message in, everyone subscribed receives it
@Payload ChatMessage message:

The @Payload annotation extracts the message payload and converts it to a ChatMessage object
Spring automatically deserializes the JSON message into your ChatMessage model
         * 
         */

        System.out.println("Received message: " + message.getSender());
        System.out.println("Received message: " + message.getContent());
        System.out.println("Received message: " + message.getChannelName());
        // You can add any additional processing logic here, such as saving the message to a database\

        return message; // Echo the received message to all subscribers


    }

}
