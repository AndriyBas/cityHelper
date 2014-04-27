package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("UserData")
public class UserData extends ParseObject {

    public void setUID(String uid) {
        put("uid", uid);
    }

    public String getUID() {
        return getString("uid");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile file) {
        put("photo", file);
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public int getRating() {
        return getInt("rating");
    }

}
