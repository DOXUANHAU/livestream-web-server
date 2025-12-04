package com.example.auth.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.request.CreateStreamerRequest;
import com.example.auth.dto.request.UpdateProfileRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.StreamerResponse;
import com.example.auth.service.StreamerService;


@RestController
@RequestMapping("/api/v1/streamers")
public class StreamerController {

    @Autowired
    private StreamerService streamerService;
    private static final Map<String, String> liveStreamKeys = new ConcurrentHashMap<>();
    
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

    // Endpoint 2: (Mới) Cho NMS gọi để báo stream bắt đầu
    @PostMapping("/start")
    public ResponseEntity<?> startStream(@RequestBody Map<String, String> payload, @RequestHeader("X-INTERNAL-TOKEN") String token) {
        // (Thêm logic kiểm tra token nội bộ)
        if (!"super-secret-stream-validation".equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String streamerName = payload.get("streamerName");
        String streamKey = payload.get("streamKey");
        liveStreamKeys.put(streamerName, streamKey);
        System.out.println("Stream started: " + streamerName + " -> " + streamKey);
        return ResponseEntity.ok().build();
    }

    // Endpoint 3: (Mới) Cho React gọi để lấy streamKey
    @GetMapping("/info/{streamerName}")
    public ResponseEntity<Map<String, String>> getStreamInfo(@PathVariable String streamerName) {
        String streamKey = liveStreamKeys.get(streamerName);

        if (streamKey == null) {
            // Trả về 404 nếu stream không live
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error", "Stream is not live or does not exist"));
        }

        // Trả về streamKey đang live
        return ResponseEntity.ok(Map.of("streamKey", streamKey));
    }
    
    // Endpoint 4: (Mới) Cho NMS gọi để báo stream kết thúc
    @PostMapping("/stop")
    public ResponseEntity<?> stopStream(@RequestBody Map<String, String> payload, @RequestHeader("X-INTERNAL-TOKEN") String token) {
        // (Kiểm tra token)
        if (!"super-secret-stream-validation".equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String streamerName = payload.get("streamerName");
        liveStreamKeys.remove(streamerName);
        System.out.println("Stream stopped: " + streamerName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<StreamerResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        ApiResponse<StreamerResponse> response = streamerService.updateProfile(request);
        
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestParam String email) {
        return ResponseEntity.ok(streamerService.getProfileByEmail(email));
    }
}
