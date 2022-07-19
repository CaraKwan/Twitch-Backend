package com.projects.recommend.entity.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//For JSON serialization and deserialization
//Used to match and parse the user auth info from frontend, using @RequestBody
public class LoginRequestBody {
    private final String userId;
    private final String password;

    @JsonCreator  //Deserialization
    public LoginRequestBody(@JsonProperty("user_id") String userId, @JsonProperty("password") String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
