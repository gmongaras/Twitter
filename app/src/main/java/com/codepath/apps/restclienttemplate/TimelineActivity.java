package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetsAdapter;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Get a twitter client so that we can make calls to the API
        client = TwitterApp.getRestClient(this);

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);

        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Configure the Recycler View: Layout Manager
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        // Configure the Recycler View: Adapter
        rvTweets.setAdapter(adapter);

        // Get the logout button
        logoutBtn = findViewById(R.id.logoutBtn);

        // Create an onClick event listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the user out
                finish();

                // Forget the user that was logged in
                client.clearAccessToken();
            }
        });




        // Populate the home timeline recycler view using the API
        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        // Call the API method to get the home timeline information
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());

                // Load in a list of tweets and notify the adapter
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJSONArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onError! " + response, throwable);
            }
        });
    }


    // Inflate the menu. Return true so that the menu is displayed and
    // false so that it's not displayed.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu by adding items to the anchor bar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    // Handle clicks on the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // If the item selected is the create tweet button (compose icon).
        if (item.getItemId() == R.id.compose) {
            Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();

            // Navigate to the composer activity
            Intent i = new Intent(this, ComposeActivity.class);
            startActivity(i);
        }

        return true;
    }
}