package com.example.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.request.SubscriptionRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.SubscriptionResponse;
import com.example.auth.service.UserActionService;

@RestController
@RequestMapping("/api/v1/user/actions")
public class UserActionController {

    @Autowired
    private UserActionService userActionService;

    @PostMapping("/subscribe")
    public ResponseEntity<?>  subscribe(@RequestBody SubscriptionRequest request) {
        // Handle subscription logic
        // return ResponseEntity.ok(new SubscriptionResponse());


            // System.out.println("Received subscription request: " + request.getSubscriberName() + " by " + request.getSubscribedToEmail());


     ApiResponse<SubscriptionResponse>   response = userActionService.subscribeUserToStreamer(request.getSubscriberName(), request.getSubscribedToEmail());


        return ResponseEntity.ok(response);

    }

    // @DeleteMapping("/unsubscribe")
    // public void unsubscribe(@RequestBody SubscriptionRequest request) {
    //     // Handle unsubscription logic
    // }
}
