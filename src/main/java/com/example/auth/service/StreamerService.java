package com.example.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auth.dto.request.CreateStreamerRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.StreamChannel;
import com.example.auth.dto.response.StreamerResponse;
import com.example.auth.model.Streamer;
import com.example.auth.model.User;
import com.example.auth.repository.StreamerRepository;
import com.example.auth.repository.UserRepository;

@Service
public class StreamerService {

    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<StreamerResponse> createStreamer(CreateStreamerRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found");
        }

        User user = userOpt.get();

        if (user.getStreamer() != null) {
            return new ApiResponse<>(false, "User already has a streamer channel");
        }

        if (streamerRepository.findByStreamerName(request.getStreamerName()).isPresent()) {
            return new ApiResponse<>(false, "Streamer name already exists");
        }

        Streamer s = new Streamer();
        s.setStreamerName(request.getStreamerName());
        s.setStreamKey(generateStreamKey());
        s.setUser(user);

        Streamer saved = streamerRepository.save(s);

        StreamerResponse response = new StreamerResponse(
            saved.getStreamerName(),
            saved.getStreamKey()
        );

        return new ApiResponse<>(true, "Streamer created successfully", response);
    }


    public ApiResponse<StreamerResponse> validateStreamKey(String streamKey) {
    Optional<Streamer> streamer = streamerRepository.findByStreamKey(streamKey);

    if (streamer.isEmpty()) {
        return new ApiResponse<>(false, "Invalid stream key");
    }

    Streamer s = streamer.get();
    StreamerResponse response = new StreamerResponse(
        s.getStreamerName(),
        s.getStreamKey()
    );

    return new ApiResponse<>(true, "Stream key valid", response);
}



    private String generateStreamKey() {
        return UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().substring(0, 8);
    }


    /**
     * Retrieves the stream channel data for a given streamer.
     *
     * @param channel The name of the streamer channel.
     * @return ApiResponse containing the StreamChannel data.
     */

    public ApiResponse<StreamChannel> getStreamChannelData(String  channelName) {
       
        Streamer streamer = streamerRepository.findByStreamerName(channelName)
                .orElseThrow(() -> new RuntimeException("Streamer not found"));


        StreamChannel response = new StreamChannel();
        response.setStreamChannel(streamer.getStreamerName());
        response.setEmail(streamer.getUser().getEmail());
        response.setName(streamer.getUser().getFullName());


        return new ApiResponse<>(true, "Streamer channel data retrieved successfully", response);
    }
}
