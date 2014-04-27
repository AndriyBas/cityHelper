package com.parse.anywall;

import android.content.Context;
import android.content.SharedPreferences;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.anywall.model.*;

public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "AnyWall";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static SharedPreferences preferences;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(AnywallPost.class);
        ParseObject.registerSubclass(Tag.class);
        ParseObject.registerSubclass(CityUser.class);
        ParseObject.registerSubclass(Issue.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(this, "iTUHhvYuaOdpVQz5aQkvnJNEd7hxZTyBxqygUPXK",
                "Le3XjYFDDJ5ZzOkUSZbqzO1ybdYfjxdqgdwAxJ40");
        preferences = getSharedPreferences("com.parse.anywall", Context.MODE_PRIVATE);
    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, 250);
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

}
