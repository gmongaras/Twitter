package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String name;
    public String screenName;
    public String publicImageUrl;

    private static final String TAG = "User";

    public User(String name, String screenName, String publicImageUrl) {
        this.name = name;
        this.screenName = screenName;
        this.publicImageUrl = publicImageUrl;
    }
    public User() {
        this.name = "";
        this.screenName = "";
        this.publicImageUrl = "";
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        // Create a new user
        User user = new User();

        // Get the JSON data
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");


        return user;
    }
}
