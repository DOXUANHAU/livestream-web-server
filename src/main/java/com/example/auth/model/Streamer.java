package com.example.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "streamers")
public class Streamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String streamerName;

    @Column(unique = true, nullable = false)
    private String streamKey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
