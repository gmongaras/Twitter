package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    public String name;
    public String username;
    public long userId;
    public String screenName;
    public String publicImageUrl;

    private static final String TAG = "User";

    public User() {
        this.name = "";
        this.username = "";
        this.userId = 0;
        this.screenName = "";
        this.publicImageUrl = "";
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        // Create a new user
        User user = new User();

        // Get the JSON data
        user.name = jsonObject.getString("name");
        user.username = jsonObject.getString("screen_name");
        user.userId = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");


        return user;
    }
}
