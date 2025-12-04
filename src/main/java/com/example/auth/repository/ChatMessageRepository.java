package com.example.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.model.ChatMessageEntity;
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByStream(String stream);

    void deleteByStream(String stream);
}
