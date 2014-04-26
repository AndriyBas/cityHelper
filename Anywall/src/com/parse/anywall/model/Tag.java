package com.parse.anywall.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by bamboo on 26.04.14.
 */

@ParseClassName("Tags")
public class Tag extends ParseObject {

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public int getCount() {
        return getInt("count");
    }

    public void incCount() {
        put("count", getCount() + 1);
    }

    public void setCount(int count) {
        put("count", count);
    }
}
