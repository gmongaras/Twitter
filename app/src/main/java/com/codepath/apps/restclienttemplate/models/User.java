package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    @ColumnInfo
    @NonNull
    @PrimaryKey // This is our primary key
    public long userId;

    @ColumnInfo
    public String name;
    @ColumnInfo
    public String username;
    @ColumnInfo
    public String screenName;
    @ColumnInfo
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


    // Give a list of tweets, turn that list into a list of users
    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }
}
