package com.codepath.apps.restclienttemplate.models;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    // When querying for recent items, we will receive some class with
    // tweets and user data.
    @Query("SELECT Tweet.id AS tweet_id, Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.retweet_count AS tweet_retweet_count, Tweet.favorite_count AS tweet_favorite_count, Tweet.favorited AS tweet_favorited, Tweet.retweeted AS tweet_retweeted, Tweet.userId AS tweet_userId, Tweet.mediaURL AS tweet_mediaURL, Tweet.media_w AS tweet_media_w, Tweet.media_h AS tweet_media_h, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.userId ORDER BY Tweet.createdAt DESC LIMIT 10")
    List<TweetWithUser> recentItems();

    // Given a Tweet object, save it into the tweet database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets); // The ellipses means it can take a variable number of tweets as an array

    // Given a User object, save it to the user database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
