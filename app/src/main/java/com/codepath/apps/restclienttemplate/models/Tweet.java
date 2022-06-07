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
    public String id;
    public User user;
    public String retweet_count;
    public String favorite_count;
    public boolean favorited;

    // Media (images) in a tweet
    public String mediaURL;
    public int media_w, media_h;

    private static final String TAG = "User";

    public Tweet() {
        this.body = "";
        this.createdAt = "";
        id = "0";
        this.user = new User();
        mediaURL = "";
        media_w = media_h = 0;
        retweet_count = "0";
        favorite_count = "0";
        favorited = false;
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
        tweet.id = jsonObject.getString("id_str");
        tweet.retweet_count = jsonObject.getString("retweet_count");
        tweet.favorite_count = jsonObject.getString("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");

        // If the tweet has media, store the media
        if (jsonObject.getJSONObject("entities").has("media")) {
            // Get the media object
            JSONObject media = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0);

            // Get the media URL and size
            tweet.mediaURL = media.getString("media_url_https");
            JSONObject sizes = media.getJSONObject("sizes").getJSONObject("thumb");
            tweet.media_w = sizes.getInt("w");
            tweet.media_h = sizes.getInt("h");
        }

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
