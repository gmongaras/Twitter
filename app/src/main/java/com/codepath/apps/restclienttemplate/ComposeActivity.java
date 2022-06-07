package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    // Max tweet size is 280 characters
    private static final int MAX_TWEET = 280;

    TextView etCompose;
    Button btnTweet;
    TwitterClient client;
    private static final String TAG = "ComposeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Get the button and text field
        etCompose = findViewById(R.id.etCompose);
        etCompose.setTextColor(Color.WHITE);
        etCompose.setLinkTextColor(Color.WHITE);
        etCompose.setHintTextColor(Color.WHITE);
        etCompose.setHighlightColor(Color.WHITE);
        btnTweet = findViewById(R.id.btnTweet);

        // Get a twitter client instance
        client = new TwitterClient(this);


        // When the button is clicked, check if the text is empty or too long
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the text field
                CharSequence text = etCompose.getText();

                // If the tweet is empty, display an error message
                if (text.length() == 0) {
                    Toast.makeText(view.getContext(), "Sorry, your tweet cannot be empty :(", Toast.LENGTH_SHORT).show();
                }

                // If the tweet is too long
                else if (text.length() > MAX_TWEET) {
                    Toast.makeText(view.getContext(), "Sorry, tweets have a 280 character limit :(", Toast.LENGTH_SHORT).show();
                }

                // If the tweet is the correct length, send a request to post
                // the tweet
                else {
                    Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

                    // Send request
                    client.publishTweet(text.toString(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet successfully published!");

                            // Create a Tweet object from the outputted JSON data
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(TAG, "Published tweet! It says: " + '"' + tweet.body + '"');

                                // If the tweet was published, create a success intent to send back
                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent); // set result code and bundle data for response
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "Unable to publish tweet");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Tweet unable to publish", throwable);
                        }
                    });
                }
            }
        });
    }
}