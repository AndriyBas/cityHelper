package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class Comment extends ParseObject {
    public ParseUser getAuthor() {
        return getParseUser("author");
    }


    public void setAuthor(ParseUser author) {
        put("author", author);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public void setIssue(Issue issue) {
        put("issue", issue);
    }

    public Issue getIssue() {
        return (Issue) getParseObject("issue");
    }

//    public void setAvatar(Bitmap avatar) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//
//        ParseFile file = new ParseFile("avatar.png", byteArray);
//        put("avatar", file);
//    }
//
//    public Bitmap getAvatar() throws ParseException {
//        ParseFile blob = getParseFile("avatar");
//        byte[] bytes = blob.getData();
//        Bitmap bmp= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        return bmp;
//    }
}
