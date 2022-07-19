package com.projects.recommend.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

//For JSON serialization and deserialization
//The object to return to user as response when successfully logged in
public class LoginResponseBody {
    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("name")
    private final String name;

    public LoginResponseBody(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
