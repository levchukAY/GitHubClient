package com.artioml.githubclient.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizedUser extends User {

    @SerializedName("private_gists")
    @Expose
    private Integer privateGists;
    @SerializedName("total_private_repos")
    @Expose
    private Integer totalPrivateRepos;
    @SerializedName("owned_private_repos")
    @Expose
    private Integer ownedPrivateRepos;
    @SerializedName("disk_usage")
    @Expose
    private Integer diskUsage;
    @SerializedName("collaborators")
    @Expose
    private Integer collaborators;
    @SerializedName("two_factor_authentication")
    @Expose
    private Boolean twoFactorAuthentication;

    public Integer getPrivateGists() {
        return privateGists;
    }

    public void setPrivateGists(Integer privateGists) {
        this.privateGists = privateGists;
    }

    public Integer getTotalPrivateRepos() {
        return totalPrivateRepos;
    }

    public void setTotalPrivateRepos(Integer totalPrivateRepos) {
        this.totalPrivateRepos = totalPrivateRepos;
    }

    public Integer getOwnedPrivateRepos() {
        return ownedPrivateRepos;
    }

    public void setOwnedPrivateRepos(Integer ownedPrivateRepos) {
        this.ownedPrivateRepos = ownedPrivateRepos;
    }

    public Integer getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Integer diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Integer getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Integer collaborators) {
        this.collaborators = collaborators;
    }

    public Boolean getTwoFactorAuthentication() {
        return twoFactorAuthentication;
    }

    public void setTwoFactorAuthentication(Boolean twoFactorAuthentication) {
        this.twoFactorAuthentication = twoFactorAuthentication;
    }

}