package com.example.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubscriptionRequest {
    private String subscriberName;      // Email of the user who subscribes
    private String subscribedToEmail;    // Email of the user being subscribed to


    public String getSubscriberName() {
        return subscriberName;
    }
   
    public String getSubscribedToEmail() {
        return subscribedToEmail;
    
    }
}
