package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserData")
public class UserData extends ParseObject {

    public void setUser(ParseUser user) {
        put("userOK", user);
    }

    public ParseUser getUser() {
        return getParseUser("userOK");
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
