package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import org.json.JSONArray;

/**
 * Created by badgateway on 26.04.14.
 */
@ParseClassName("Issues")
public class Issue extends ParseObject {

    public void setTitle(String title) {
        put("title", title);
    }

    public String getTitle() {
        return getString("title");
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
}
