package com.example.auth.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRespone {
     private String stream;   // tên stream (channelName)
    List<ChatItem> chats ;
  }
   

   
   
