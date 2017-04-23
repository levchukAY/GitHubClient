package com.artioml.githubclient.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

}