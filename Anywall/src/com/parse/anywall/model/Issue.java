package com.parse.anywall.model;

import com.parse.anywall.model.photo.Photo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by badgateway on 26.04.14.
 */
public class Issue {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_STATUS = "tratus";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

    private static final String JSON_TAGS = "tags";


    private Photo mPhoto;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mStatus;
    private String mSuspect;

    public Issue() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }



    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }



    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    @Override
    public String toString() {
        return mTitle;

    }
}
