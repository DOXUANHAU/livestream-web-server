package com.example.socket.model;

public class ChatMessage {
    private String sender;
    private String content;
    private String label;  // classified result
    private Float score;   // raw score or prediction value
    private String channelName; // channel name

    // Getters and setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Float getScore() { return score; }
    public void setScore(Float score) { this.score = score; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
}
