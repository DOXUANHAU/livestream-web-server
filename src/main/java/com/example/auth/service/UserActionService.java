package com.example.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.SubscriptionResponse;
import com.example.auth.model.User;
import com.example.auth.model.UserSubscription;
import com.example.auth.repository.StreamerRepository;
import com.example.auth.repository.UserActionRepository;
import com.example.auth.repository.UserRepository;

@Service
public class UserActionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private UserActionRepository subscriptionRepository;



    public ApiResponse<SubscriptionResponse> subscribeUserToStreamer(String streamChannel, String userEmail) {
        // Implement subscription logic here
        // For example, find the user and streamer by their identifiers,
        // create a subscription record, and save it to the database.

        // get streamer ID
        User streamer = streamerRepository.findByStreamerName(streamChannel)
                .orElseThrow(() -> new RuntimeException("Streamer not found"))
                .getUser();

        // get user ID
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Create subscription record
        UserSubscription subscription = new UserSubscription();
        subscription.setSubscriber(user);
        subscription.setSubscribedTo(streamer);

        UserSubscription  savedSubscription = subscriptionRepository.save(subscription);
 // Return success response
            SubscriptionResponse response = new SubscriptionResponse();
            response.setId(savedSubscription.getId());
            response.setSubscriberId(savedSubscription.getSubscriber().getId());
            response.setSubscribedToId(savedSubscription.getSubscribedTo().getId());


            if (savedSubscription != null) {
                response.setStatus(true);
            } else {
                response.setStatus(false);
            }

        // Save subscription to the database

            return new ApiResponse<SubscriptionResponse>(true, "Subscription", response);

        // Placeholder response
    }


}
