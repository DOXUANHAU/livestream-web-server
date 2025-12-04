package com.example.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auth.dto.request.CreateStreamerRequest;
import com.example.auth.dto.request.UpdateProfileRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.StreamChannel;
import com.example.auth.dto.response.StreamerResponse;
import com.example.auth.dto.response.UserProfileResponse;
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

        // SỬA ĐỔI TẠI ĐÂY
        if (user.getStreamer() != null) {
        // Nếu user đã có streamer, lấy thông tin cũ
        Streamer existingStreamer = user.getStreamer();

        // Tạo response DTO từ thông tin cũ
        StreamerResponse response = new StreamerResponse(
        existingStreamer.getStreamerName(),
        existingStreamer.getStreamKey()
        );

        // Trả về THÀNH CÔNG (true) với data cũ
        return new ApiResponse<>(true, "Streamer channel already exists, returning existing data.", response);
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

public ApiResponse<StreamerResponse> updateProfile(UpdateProfileRequest request) {
        // 1. Tìm User
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found");
        }
        User user = userOpt.get();

        // 2. Cập nhật thông tin cá nhân (User)
        user.setFullName(request.getFullName());
        user.setDob(request.getDob());
        user.setGender(request.getGender());

        // 3. Cập nhật thông tin kênh (Streamer)
        Streamer streamer = user.getStreamer();
        if (streamer != null) {
            String newStreamerName = request.getStreamerName();
            String oldStreamerName = streamer.getStreamerName();

            // Nếu tên kênh thay đổi, cần kiểm tra trùng lặp
            if (!newStreamerName.equals(oldStreamerName)) {
                if (streamerRepository.findByStreamerName(newStreamerName).isPresent()) {
                    return new ApiResponse<>(false, "Streamer name already exists");
                }
                streamer.setStreamerName(newStreamerName);
            }
        } else {
            // Trường hợp User chưa có kênh nhưng muốn update (Tùy logic của bạn có cho phép hay không)
            // Ở đây ta có thể tạo mới hoặc báo lỗi. 
            // Giả sử logic là chỉ update nếu đã có kênh:
            return new ApiResponse<>(false, "Streamer profile not found for this user");
        }

        // 4. Lưu xuống DB (Cascade sẽ lưu cả User và Streamer)
        userRepository.save(user);

        // 5. Trả về data mới
        StreamerResponse response = new StreamerResponse(
            streamer.getStreamerName(),
            streamer.getStreamKey()
        );

        return new ApiResponse<>(true, "Profile updated successfully", response);
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

    public ApiResponse<UserProfileResponse> getProfileByEmail(String email) {
        // 1. Tìm User trong DB
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found");
        }

        User user = userOpt.get();
        Streamer streamer = user.getStreamer();

        // 2. Xử lý trường hợp User chưa có kênh Stream (để tránh lỗi NullPointerException)
        String currentStreamerName = "";
        String currentStreamKey = "";
        
        if (streamer != null) {
            currentStreamerName = streamer.getStreamerName();
            currentStreamKey = streamer.getStreamKey();
        }

        // 3. Tạo DTO trả về
        UserProfileResponse profile = new UserProfileResponse(
            user.getFullName(),
            user.getEmail(),
            user.getDob(),
            user.getGender(),
            currentStreamerName,
            currentStreamKey
        );

        return new ApiResponse<>(true, "Profile retrieved successfully", profile);
    }
}
