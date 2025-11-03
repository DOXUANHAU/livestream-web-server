package com.example.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.model.UserSubscription;

public interface UserActionRepository extends JpaRepository<UserSubscription, Long> {


    // /**
    //  * 
    //  * paramto user subscription to streamer add record
    //  * @param userId 
    //  * 
    //  * 
    //  * @param streamerId
    //  * @return
    //  */
    // @Query(value = "INSERT INTO user_subcriptions (subscriber_id, subscribed_to_id) VALUES (:subscriberId, :streamerId) RETURNING *", nativeQuery = true)
    // Optional<UserSubcriptions> subscribeUserToStreamer(
    //         @Param("subscriberId") Long subscriberId,
    //         @Param("streamerId") Long streamerId
    // );

    
}
