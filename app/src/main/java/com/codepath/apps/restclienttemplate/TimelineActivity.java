package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    Button reply;

    // Used for swipe reloading
    private SwipeRefreshLayout swipeContainer;

    // Used for endless scrolling
    private EndlessRecyclerViewScrollListener scrollListener;

    // The smallest id seen so far
    long smallest_id;

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

        // Set the smallest id as the largest long value
        smallest_id = 9223372036854775807L;

        // Configure the Recycler View: Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        // Configure the Recycler View: Adapter
        rvTweets.setAdapter(adapter);

        // Get the logout button
        logoutBtn = findViewById(R.id.logoutBtn);

        // Create an onClick event listener for the logout button
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




        // Enable endless scrolling
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);




        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);

                // We are refreshing the page
                swipeContainer.setRefreshing(false);

                // We are no longer refreshing the page
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    // This method sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        // Call the API method to get the home timeline information
        client.getTweets(String.valueOf(smallest_id-1), new JsonHttpResponseHandler() {
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

                // Get the smallest id seen so far
                for (int i = 0; i < tweets.size(); i++) {
                    if (Long.parseLong(tweets.get(i).id) < smallest_id) {
                        smallest_id = Long.parseLong(tweets.get(i).id);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onError! " + response, throwable);
            }
        });
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

                    // Get the smallest id seen so far
                    for (int i = 0; i < tweets.size(); i++) {
                        if (Long.parseLong(tweets.get(i).id) < smallest_id) {
                            smallest_id = Long.parseLong(tweets.get(i).id);
                        }
                    }
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

            // Put the compose activity in compose mode
            i.putExtra("mode", "compose");

            // Start the intent, but send data back to the parent. So instead of
            // using startActivity, we use startActivityForResult
            startActivityForResult(i, REQUEST_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // If a tweet is successfully sent (through the onOptionsItemSelected, startActivityForResult
    // method), we should handle that response and update the recycler view
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
            Log.e(TAG, "Issue with publishing tweet. Code: " + String.valueOf(resultCode));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    // Class used for reloading the page
    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // the data has come back, add new items to your adapter
                adapter.addAll(tweets);
                // Reset the smallest id as the largest long value
                smallest_id = 9223372036854775807L;
                // Get the smallest id seen so far
                for (int i = 0; i < tweets.size(); i++) {
                    if (Long.parseLong(tweets.get(i).id) < smallest_id) {
                        smallest_id = Long.parseLong(tweets.get(i).id);
                    }
                }
                // Populate the timeline with new data
                populateHomeTimeline();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });
    }
}