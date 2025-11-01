package com.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamerResponse {
    private String streamerName;
    private String streamKey;

}
