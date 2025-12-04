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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socket.model.ChatMessage;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

@Controller
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private OrtEnvironment env;
    private OrtSession session;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
         try {
            System.out.println("ToxicController constructor start");
            env = OrtEnvironment.getEnvironment();

            // Lấy resource stream
            InputStream modelStream = getClass().getResourceAsStream("/models/toxic_model.onnx");
            if (modelStream == null) {
                throw new FileNotFoundException("Cannot find toxic_model.onnx in resources");
            }

            // Copy sang file tạm
            Path tempFile = Files.createTempFile("toxic_model", ".onnx");
            Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            modelStream.close();

            System.out.println("Temporary ONNX model path: " + tempFile.toAbsolutePath());

            // Tạo ONNX session từ file tạm
            session = env.createSession(tempFile.toAbsolutePath().toString(), new OrtSession.SessionOptions());
            System.out.println("ONNX session created successfully");

        } catch (Exception e) {
            System.err.println("Failed to initialize ToxicController: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

         try {
             String text = message.getContent().replaceAll("[\\p{So}\\p{Cn}\\p{Cs}]", "<emoji>");
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, new String[][]{{text}});
        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("string_input", inputTensor);

        OrtSession.Result result = session.run(inputs);

        long[] output = (long[]) result.get(0).getValue(); 

        long prediction = output[0];
        
        String label = (prediction == 1) ? "TOXIC" : "NOT TOXIC";

        
        message.setLabel(label);
        message.setScore((float) prediction);



        // Send to all subscribers of this specific channel
        messagingTemplate.convertAndSend("/topic/messages/" + channelName, message);
      } catch (Exception e) {
        e.printStackTrace();
        messagingTemplate.convertAndSend("/topic/messages/" + channelName, message);
      }
    }


}
