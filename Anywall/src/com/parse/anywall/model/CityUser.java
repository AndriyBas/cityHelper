package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by bamboo on 27.04.14.
 */

@ParseClassName("Users")
public class CityUser extends ParseUser {

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
