package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

// This class is the combination of Tweet attributes and User attributes which the
// SQL query will return due to the Foreign key
public class TweetWithUser {

    // @Embedded notation flattens the properties of the User object
    // into the object, preserving encapsulation.
    // Basically embed all attributes of the User class into this class
    @Embedded
    User user;

    // Prefix prepends "tweet_" to every attribute name from the Tweet object
    @Embedded(prefix = "tweet_")
    Tweet tweet;


    // Given a list of TweetWithUser, translate this to a list of Tweet objects
    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < tweetWithUsers.size(); i++) {
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.user = tweetWithUsers.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }
}
