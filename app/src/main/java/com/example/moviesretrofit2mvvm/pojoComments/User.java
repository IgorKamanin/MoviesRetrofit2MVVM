package com.example.moviesretrofit2mvvm.pojoComments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("links")
    @Expose
    private Links__4 links;

    public Links__4 getLinks() {
        return links;
    }

    public void setLinks(Links__4 links) {
        this.links = links;
    }
}
