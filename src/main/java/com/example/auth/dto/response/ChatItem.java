package com.example.auth.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ChatItem {
    private String name,chat;

    public ChatItem(String name, String chat) {
        this.name = name;
        this.chat = chat;
    }


}
