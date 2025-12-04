package com.example.auth.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auth.model.ChatMessageEntity;
import com.example.auth.repository.ChatMessageRepository;

@Service
public class ChatService {

    @Autowired
    private final ChatMessageRepository repo;

    public ChatService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    public void saveMessage(String stream, String sender, String content) {
        ChatMessageEntity msg = new ChatMessageEntity(stream, sender, content);
        repo.save(msg);
    }

    public List<ChatMessageEntity> getMessages(String stream) {
        return repo.findByStream(stream);
    }

    public void clearMessages(String stream) {
        repo.deleteByStream(stream);
    }
}
