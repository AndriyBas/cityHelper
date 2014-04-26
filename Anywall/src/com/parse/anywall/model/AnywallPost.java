package com.parse.anywall.model;

import com.parse.*;

/**
 * Data model for a post.
 */
@ParseClassName("Posts")
public class AnywallPost extends ParseObject {

    public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setDescription(String desc) {
        put("desc", desc);
    }

    public String getDescription() {
        return getString("desc");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public static ParseQuery<AnywallPost> getQuery() {
        return ParseQuery.getQuery(AnywallPost.class);
    }
}
