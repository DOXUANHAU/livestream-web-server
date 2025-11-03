package com.example.auth.dto.response;

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
public class SubscriptionResponse {
    private Long id;                // Subscription record ID
    private Long subscriberId;      // ID of the user who subscribes
    private Long subscribedToId;    // ID of the user being subscribed to
    private Boolean status;
}
