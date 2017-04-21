package com.artioml.githubclient.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateData {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("blog")
    @Expose
    private String blog;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("hireable")
    @Expose
    private Boolean hireable;
    @SerializedName("bio")
    @Expose
    private String bio;

    public UpdateData(String name, String email,
                      String blog, String company, String location, Boolean hireable, String bio) {
        this.name = name;
        this.email = email;
        this.blog = blog;
        this.company = company;
        this.location = location;
        this.hireable = hireable;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getHireable() {
        return hireable;
    }

    public void setHireable(Boolean hireable) {
        this.hireable = hireable;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}
