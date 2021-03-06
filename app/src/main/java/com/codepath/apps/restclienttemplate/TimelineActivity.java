package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.User;
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
    private ActivityTimelineBinding binding;

    // Used for SQL database connection
    TweetDao tweetDao;

    // Instance of the progress action-view
    MenuItem miActionProgressItem;

    // Used for swipe reloading
    private SwipeRefreshLayout swipeContainer;

    // Used for endless scrolling
    private EndlessRecyclerViewScrollListener scrollListener;

    // The smallest id seen so far
    long smallest_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get a twitter client so that we can make calls to the API
        client = TwitterApp.getRestClient(this);

        // Initialize the SQL database object
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

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
        //populateHomeTimeline();
        // Node ^ this was moved to onPrepareOptionsMenu in order to
        // create the menu before loading in the tweets to show the progress bar



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


    // Used for progress bar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Populate the home timeline recycler view using the API
        populateHomeTimeline();

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }
    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }


    // This method sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        // Call the API method to get the home timeline information
        showProgressBar();
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
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onError! " + response, throwable);
                //hideProgressBar();
            }
        });
    }


    private void populateHomeTimeline() {
        // Call the API method to get the home timeline information
        showProgressBar();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                showProgressBar();
                Log.i(TAG, "onSuccess! " + json.toString());

                // Load in a list of tweets and notify the adapter
                JSONArray jsonArray = json.jsonArray;
                try {
                    // List of new tweets from the API call
                    List<Tweet> tweetsFromNetwork = Tweet.fromJSONArray(jsonArray);

                    // List of Users from the API call
                    List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);

                    // Add the tweets to the Recycler View
                    tweets.addAll(tweetsFromNetwork);
                    adapter.notifyDataSetChanged();

                    // Get the smallest id seen so far
                    for (int i = 0; i < tweets.size(); i++) {
                        if (Long.parseLong(tweets.get(i).id) < smallest_id) {
                            smallest_id = Long.parseLong(tweets.get(i).id);
                        }
                    }

                    // After all tweets have been loaded in, save the tweets to the database
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into database");

                            // Save every Tweet and User to the database.
                            // Note: Since the Users are the child of the Foreign key,
                            // They must be populated first
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onError! " + response, throwable);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });

                // If the tweets could not be loaded in through the API, load
                // in the tweets from the database...
                // Make the SQL query for existing tweets in the DB. Make sure this
                // is happening on a background thread since it's an expensive
                // operation.
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Showing data from database");
                        List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();

                        // Get all the tweets from the database
                        List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);

                        reloadOffline(tweetsFromDB);
                    }
                });

            }
        });
    }

    // Reload the recycler view when offline
    public void reloadOffline(List<Tweet> tweetsFromDB) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Clear the adapter and add all saved tweets to the adapter
                adapter.clear();
                adapter.addAll(tweetsFromDB);
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