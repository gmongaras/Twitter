package com.codepath.apps.restclienttemplate.models;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;

    private static final String TAG = "User";

    public Tweet() {
        this.body = "";
        this.createdAt = "";
        this.user = new User();
    }

    // Load in JSON data into a new Tweet
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        // Create the tweet
        Tweet tweet = new Tweet();

        // Get the JSON data
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));


        return tweet;
    }

    // Given a list of JSON data, create a list of tweets
    public static List<Tweet> fromJSONArray(JSONArray jsonArray) throws JSONException {
        // Create a new list of tweets
        List<Tweet> tweets = new ArrayList<>();

        // Iterate over all tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }
}
