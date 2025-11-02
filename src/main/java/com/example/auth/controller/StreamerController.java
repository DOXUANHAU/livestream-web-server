package com.example.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.request.CreateStreamerRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.StreamerResponse;
import com.example.auth.service.StreamerService;


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
public ResponseEntity<?> validate(
        @RequestHeader(value = "X-INTERNAL-TOKEN", required = false) String internalToken,
        @RequestBody Map<String, String> payload) {

    System.out.println("Received X-INTERNAL-TOKEN: " + internalToken);

    if (!"super-secret-stream-validation".equals(internalToken)) {
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
    }

    String streamKey = payload.get("streamKey");
    return ResponseEntity.ok(streamerService.validateStreamKey(streamKey));
}


 
    @GetMapping("/channel")
public ResponseEntity<?> getStreamChannel(@RequestParam String channelName) {
    return ResponseEntity.ok(streamerService.getStreamChannelData(channelName));
}

}
