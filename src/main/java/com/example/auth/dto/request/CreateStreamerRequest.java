package com.example.auth.dto.request;

import lombok.Data;

@Data
public class CreateStreamerRequest {
    private String email;
    private String streamerName;
}
