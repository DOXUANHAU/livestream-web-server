package com.example.auth.repository;

import com.example.auth.model.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StreamerRepository extends JpaRepository<Streamer, Long> {
    Optional<Streamer> findByStreamKey(String streamKey);
    Optional<Streamer> findByStreamerName(String streamerName);
}
