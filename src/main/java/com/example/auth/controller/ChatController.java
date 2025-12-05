package com.example.auth.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.response.ChatItem;
import com.example.auth.dto.response.ChatRespone;
import com.example.auth.model.ChatMessageEntity;
import com.example.auth.service.ChatService;
@RestController
@RequestMapping("/api/v1/user/chat")
public class ChatController {
    @Autowired
     private ChatService chatService;

     /** 🧡 Khi user join → load toàn bộ lịch sử chat */
    @GetMapping("/history/{stream}")
    public ResponseEntity<?> getHistory(@PathVariable String stream) {
        List<ChatMessageEntity> messages = chatService.getMessages(stream);
        List<ChatItem> chats = new ArrayList<>();
        messages.forEach(msg -> {
            ChatItem item = new ChatItem(msg.getSender(), msg.getContent());
            chats.add(item);
        });



        ChatRespone respone   = new ChatRespone(stream, chats);


        //  return a form that UI can take it in react and display
        return ResponseEntity.ok(respone);
    }

    /** ❌ Khi streamer tắt live → xoá toàn bộ chat */
    @DeleteMapping("/clear/{stream}")
    public void clearChat(@PathVariable String stream) {
        chatService.clearMessages(stream);
    }
}
