package com.artioml.githubclient.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizedUser extends User {

    @SerializedName("private_gists")
    @Expose
    private int privateGists;
    @SerializedName("total_private_repos")
    @Expose
    private int totalPrivateRepos;
    @SerializedName("owned_private_repos")
    @Expose
    private int ownedPrivateRepos;

    public int getPrivateGists() {
        return privateGists;
    }

    public int getTotalPrivateRepos() {
        return totalPrivateRepos;
    }

    public int getOwnedPrivateRepos() {
        return ownedPrivateRepos;
    }

}