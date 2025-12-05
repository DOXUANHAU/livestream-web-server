package com.example.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;





@Entity
@Data
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stream;   // tên stream (channelName)
    private String sender;
    private String content;

    // ⚠️ Hibernate bắt buộc phải có constructor rỗng
    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String stream, String sender, String content) {
        this.stream = stream;
        this.sender = sender;
        this.content = content;
}

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}