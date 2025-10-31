package com.example.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.auth.dto.request.CreateStreamerRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.StreamerResponse;
import com.example.auth.service.StreamerService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/streamers")
public class StreamerController {

    @Autowired
    private StreamerService streamerService;

    @PostMapping("/create")
    public ApiResponse<StreamerResponse> createStreamer(@RequestBody CreateStreamerRequest request) {
        return streamerService.createStreamer(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, String> payload) {
        String streamKey = payload.get("streamKey");
        return ResponseEntity.ok(streamerService.validateStreamKey(streamKey));
    }
}
