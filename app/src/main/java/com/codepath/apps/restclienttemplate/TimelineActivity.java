package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    // Unique request code for response
    public final int REQUEST_CODE = 20;

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
            // Navigate to the composer activity
            Intent i = new Intent(this, ComposeActivity.class);

            // Start the intent, but send data back to the parent. So instead of
            // using startActivity, we use startActivityForResult
            startActivityForResult(i, REQUEST_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // If a tweet is successfully sent (through the onOptionsItemSelected, startActivityForResult
    // method), we should handle that response and update the racycler view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If the request code is the same as the request code for
        // the tweet publication and the result is OK, handle the response
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get data from the intent that published the tweet
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            // Update the Recycler View with this new tweet
            tweets.add(0, tweet); // Add a tweet to the beginning of the list
            adapter.notifyItemInserted(0); // Notify the RV that a change was made
            rvTweets.smoothScrollToPosition(0); // Go to the top of the RV
        }
        else {
            Log.e(TAG, "Issue with publishing tweet. Code: " + String.valueOf(requestCode));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}