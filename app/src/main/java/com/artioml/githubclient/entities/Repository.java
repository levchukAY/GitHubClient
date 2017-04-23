package com.artioml.githubclient.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repository {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("private")
    @Expose
    private boolean _private;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("language")
    @Expose
    private String language;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPrivate() {
        return _private;
    }

    public void setPrivate(boolean _private) {
        this._private = _private;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}