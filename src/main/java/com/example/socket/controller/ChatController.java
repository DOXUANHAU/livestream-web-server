package com.example.socket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socket.model.ChatMessage;

@Controller
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 📨 Handle real-time messages sent from clients via WebSocket
     * Client sends to: /app/chat/{channelName}
     * Server broadcasts to: /topic/messages/{channelName}
     */
    @MessageMapping("/chat/{channelName}")
    public void sendMessage(
            @DestinationVariable String channelName,
            @Payload ChatMessage message
    ) {


        // Send to all subscribers of this specific channel
        messagingTemplate.convertAndSend("/topic/messages/" + channelName, message);
    }


}
