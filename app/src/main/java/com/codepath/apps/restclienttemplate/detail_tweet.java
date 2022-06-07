package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class detail_tweet extends AppCompatActivity {

    // References to each item in the detailed tweet view
    ImageView ivProfileImage_det;
    TextView tvBody_det;
    TextView tvScreenName_det;
    TextView tvUsername_det;
    TextView tvTimestamp_det;
    ImageView ivMedia_det;
    TextView retweet_ct_det;
    TextView like_ct_det;
    boolean favorited;

    TwitterClient client;

    private static final String TAG = "detail_tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);

        // Get a reference to each item in the tweet view
        ivProfileImage_det = findViewById(R.id.ivProfileImage_det);
        tvBody_det = findViewById(R.id.tvBody_det);
        tvScreenName_det = findViewById(R.id.tvScreenName_det);
        tvUsername_det = findViewById(R.id.tvUsername_det);
        tvTimestamp_det = findViewById(R.id.tvTimestamp_det);
        ivMedia_det = findViewById(R.id.ivMedia_det);
        retweet_ct_det = findViewById(R.id.retweet_ct_det);
        like_ct_det = findViewById(R.id.like_ct_det);

        // Get a twitter client instance
        client = new TwitterClient(this);


        // Make a request to get the tweet information and load it in
        String id = getIntent().getExtras().getString("id"); // From putExtra
        client.getTweetInfo(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Tweet received!");

                // Get the JSON object
                JSONObject json_obj = null;
                try {
                    json_obj = json.jsonArray.getJSONObject(0);
                } catch (JSONException e) {
                    Log.e(TAG, "Issue getting tweet information", e);
                }

                // Using the JSON data, fill in the tweet text
                Tweet tweet;
                try {
                    tweet = Tweet.fromJson(json_obj);

                    // Load in the tweet text information
                    tvBody_det.setText(tweet.body);
                    tvScreenName_det.setText(tweet.user.screenName);
                    tvUsername_det.setText("@" + tweet.user.username);
                    tvTimestamp_det.setText(tweet.createdAt);
                    retweet_ct_det.setText(tweet.retweet_count);
                    like_ct_det.setText(tweet.favorite_count);
                    favorited = tweet.favorited;

                    // Load in the profile image
                    Glide.with(ivProfileImage_det)
                            .load(tweet.user.publicImageUrl)
                            .circleCrop()
                            .into(ivProfileImage_det);

                    // If media is present, load in the media image
                    if (tweet.mediaURL.length() > 0) {
                        // Make the view have spacial dimensions
                        ivMedia_det.getLayoutParams().height = -2;
                        ivMedia_det.getLayoutParams().width = -1;

                        // Load in the image
                        Glide.with(ivMedia_det)
                                .load(tweet.mediaURL)
                                .fitCenter()
                                .apply(new RequestOptions().transform(new RoundedCorners(50)))
                                .into(ivMedia_det);

                        // Make the view visible
                        ivMedia_det.setVisibility(View.VISIBLE);
                    }
                    // If media is not present, make the view invisible
                    else {
                        ivMedia_det.getLayoutParams().height = 0;
                        ivMedia_det.getLayoutParams().width = 0;
                        ivMedia_det.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Issue parsing json into tweet", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error in tweet lookup: " + response, throwable);
            }
        });
    }
}