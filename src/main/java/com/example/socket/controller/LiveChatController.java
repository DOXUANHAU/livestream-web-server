package com.example.socket.controller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.service.ChatService;
import com.example.socket.model.ChatMessage;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

@RestController
@RequestMapping("/chat")
public class LiveChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;  // <-- FIX NPE here

    private OrtEnvironment env;
    private OrtSession session;

    public LiveChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;

        try {
            env = OrtEnvironment.getEnvironment();
            InputStream modelStream = getClass().getResourceAsStream("/models/toxic_model.onnx");
            if (modelStream == null) throw new FileNotFoundException("Model not found");

            Path tempFile = Files.createTempFile("toxic_model", ".onnx");
            Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            modelStream.close();

            session = env.createSession(tempFile.toAbsolutePath().toString(), new OrtSession.SessionOptions());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 📨 Nhận tin nhắn realtime */
    @MessageMapping("/chat/{channelName}")
    public void sendMessage(
            @DestinationVariable String channelName,
            @Payload ChatMessage message
    ) {

        try {
            // ---- toxic filter ----
            String text = message.getContent().replaceAll("[\\p{So}\\p{Cn}\\p{Cs}]", "<emoji>");
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, new String[][]{{text}});
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("string_input", inputTensor);

            long prediction = (long) ((long[]) session.run(inputs).get(0).getValue())[0];
            message.setLabel(prediction == 1 ? "TOXIC" : "NOT TOXIC");
            message.setScore((float) prediction);

            // ⬇ Save vào DATABASE nếu không toxic
            if (prediction == 0) {
                chatService.saveMessage(channelName, message.getSender(), message.getContent());
            }

            // ⬇ Gửi realtime về các client
            messagingTemplate.convertAndSend("/topic/messages/" + channelName, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
}
