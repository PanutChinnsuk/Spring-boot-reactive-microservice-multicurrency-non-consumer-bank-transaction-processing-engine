package com.panut.centralbanking.userservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.userservice.api.service.customerservice.response.UserDefaultPasswordData;
import com.panut.centralbanking.userservice.core.UserStatus;
import com.panut.centralbanking.userservice.infrastructure.model.User;
import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedData {
    private String userId;
    private UserStatus userStatus;
    private LocalDateTime createdAt;
    private String username;

    @JsonIgnore
    private UserDefaultPasswordData defaultData;

    public Mono<UserCreatedData> defaultData(final UserDefaultPasswordData data) {
        return Mono.defer(() -> {
            this.defaultData = data;


            return Mono.just(this);
        });
    }

    public Mono<UserCreatedData> user(final User user) {
        return Mono.defer(() -> {
            this.userId = user.getId();
            this.userStatus = user.getUserStatus();
            this.createdAt = user.getCreatedAt();

            return Mono.just(this);
        });
    }

    public Mono<UserProfile> userProfile(final UserProfile userProfile) {
        return Mono.defer(() -> {
            this.username = userProfile.getUsername();

            return Mono.just(userProfile);
        });
    }
}
