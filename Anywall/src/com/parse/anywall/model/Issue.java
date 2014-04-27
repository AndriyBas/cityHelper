package com.parse.anywall.model;


import com.parse.*;
import org.json.JSONArray;

import java.io.Serializable;


/**
 * Created by badgateway on 26.04.14.
 */

@ParseClassName("Issues")
public class Issue extends ParseObject implements Serializable {

    public void setTitle(String title) {
        put("title", title);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setDetail(String detail) {
        put("details", detail);
    }

    public String getDetail() {
        return getString("details");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

    public JSONArray getTags() {
        return getJSONArray("tags");
    }

    public void setTags(JSONArray tags) {
        put("tags", tags);
    }

    public void setParticipants(int p) {
        put("participants", p);
    }

    public int getParticipants() {
        return getInt("participants");
    }

    public void setDonation(int d) {
        put("donation", d);
    }

    public int getDonation() {
        return getInt("donation");
    }

    public long getDate() {
        return getLong("date");
    }

    public void setDate(long date) {
        put("date", date);

    }

    public void setAuthor(ParseUser author) {
        put("author", author);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }


    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }
}
