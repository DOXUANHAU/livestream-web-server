package com.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamerResponse {
    private String username;
    private String streamKey;

}
