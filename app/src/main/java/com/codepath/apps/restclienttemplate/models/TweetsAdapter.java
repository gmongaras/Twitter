package com.codepath.apps.restclienttemplate.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.detail_tweet;
import com.codepath.apps.restclienttemplate.userActivity;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

// The tweets adapter uses the Recycler View to display the list of tweets
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;
    private static final String TAG = "TweetsAdapter";


    // Pass in the context and list of tweets as a constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout for a tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view and inflate it in the Recycler View
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);

        // Return the view
        return new ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the data/element at the given position
        Tweet tweet = tweets.get(position);

        // Get a twitter client instance
        TwitterClient client = new TwitterClient(context);

        // When the view (tweet) is clicked, we want to display a
        // detailed view of that tweet
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the detailed view window
                Context context = v.getContext();
                Intent i = new Intent(context, detail_tweet.class);
                i.putExtra("id", tweets.get(position).id);
                context.startActivity(i);
            }
        });

        // Add an on click listener for the like icon
        holder.itemView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favorited == true) {
                    // Send a request to unlike this tweet
                    client.unlikeTweet(Long.parseLong(tweet.id), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet liked");

                            // Change the image to an unliked heart
                            Glide.with(context)
                                    .load(R.drawable.heart)
                                    .into((ImageView) view.findViewById(R.id.like));
                            holder.favorited = false;

                            // Decrease the like count
                            holder.like_ct.setText(String.valueOf(Integer.parseInt(holder.like_ct.getText().toString())-1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Tweet like issue", throwable);
                        }
                    });
                }
                else {
                    // Send a request to like this tweet
                    client.likeTweet(Long.parseLong(tweet.id), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet liked");

                            // Change the image to a liked heart
                            Glide.with(context)
                                    .load(R.drawable.heart_filled)
                                    .into((ImageView) view.findViewById(R.id.like));
                            holder.favorited = true;

                            // Increase the like count
                            holder.like_ct.setText(String.valueOf(Integer.parseInt(holder.like_ct.getText().toString())+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Tweet like issue", throwable);
                        }
                    });
                }
            }
        });

        // Add an onclick listener for the retweet icon
        holder.itemView.findViewById(R.id.retweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the tweet has already been retweeted, unretweet it
                if (holder.retweeted == true) {
                    // Send a request to unretweet this tweet
                    client.unretweetTweet(Long.parseLong(tweet.id), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet unretweeted");

                            // Change the image to an unliked heart
                            Glide.with(context)
                                    .load(R.drawable.retweet)
                                    .into((ImageView) view.findViewById(R.id.retweet));
                            holder.retweeted = false;

                            // Decrease the retweet count
                            holder.retweet_ct.setText(String.valueOf(Integer.parseInt(holder.retweet_ct.getText().toString()) - 1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Tweet unretweet issue", throwable);
                        }
                    });
                }
                // If the tweet has not been retweeted, retweet it
                else {
                    // Send a request to retweet this tweet
                    client.retweetTweet(Long.parseLong(tweet.id), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet retweeted");

                            // Change the image to an unliked heart
                            Glide.with(context)
                                    .load(R.drawable.retweet_filled)
                                    .into((ImageView) view.findViewById(R.id.retweet));
                            holder.retweeted = true;

                            // Increase the retweet count
                            holder.retweet_ct.setText(String.valueOf(Integer.parseInt(holder.retweet_ct.getText().toString()) + 1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Tweet retweet issue", throwable);
                        }
                    });
                }
            }
        });

        // When the reply button is clicked, open up a new window
        // to compose a new tweet as a reply to the current tweet
        holder.itemView.findViewById(R.id.reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to compose a tweet
                Intent i = new Intent(view.getContext(), ComposeActivity.class);

                // Set the mode as a reply
                i.putExtra("mode", "reply");

                // Pass in the username to reply to
                i.putExtra("username", tweet.user.username);

                // Pass in the tweet id
                i.putExtra("replyId", tweet.id);

                // Start the compose tweet view
                context.startActivity(i);
            }
        });

        // Add an on click listener for the user icon
        holder.ivProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.getFollowers(tweet.user.userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Followers retrieved!");

                        try {
                            // Get all users from the json data
                            String users = json.jsonObject.getJSONArray("users").toString();

                            // Create an intent to load the users page
                            Intent i = new Intent(context, userActivity.class);

                            // Store user follower information
                            i.putExtra("users", users);

                            // Store the user information
                            i.putExtra("profileImg", tweet.user.publicImageUrl);
                            i.putExtra("username", tweet.user.username);

                            // Show the followers page
                            context.startActivity(i);

                        } catch (JSONException e) {
                            Log.e(TAG, "Issue getting users from the followers data");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Followers not retrieved :(");
                    }
                });
            }
        });

        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Define a view holder to store information about the tweet layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        // References to each item in the tweet view
        ImageView ivProfileView;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvUsername;
        TextView tvTimestamp;
        ImageView ivMedia;
        TextView retweet_ct;
        ImageView retweet;
        TextView like_ct;
        ImageView like;

        // Did the user favorite the tweet?
        boolean favorited;

        // Did the user retweet the tweet?
        boolean retweeted;

        // Given a view to store a tweet, populate that view
        // with tweet information
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get a reference to each item in the tweet view
            ivProfileView = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            retweet_ct = itemView.findViewById(R.id.retweet_ct);
            retweet = itemView.findViewById(R.id.retweet);
            like_ct = itemView.findViewById(R.id.like_ct);
            like = itemView.findViewById(R.id.like);
        }

        // Convert a numeric string to a string with a K in it
        // Ex: 1500 -> 1.5 K
        public String convertNum(String num) {
            // Convert the string to an integer
            int n = Integer.parseInt(num);

            // Check if the value is above 1000
            if (n / 1000 > 0) {
                // Divide the integer by 1000 and store it
                // as a string with a K in it
                num = String.valueOf(n/1000) + "K";
            }

            // Return the number as a string
            return num;
        }

        // Given a tweet, save the data form the tweet into this object
        public void bind(Tweet tweet) {
            // Load in the text data
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvUsername.setText("@" + tweet.user.username);
            tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));
            retweet_ct.setText(convertNum(tweet.retweet_count));
            like_ct.setText(convertNum(tweet.favorite_count));

            // Get the favorited state
            favorited = tweet.favorited;
            retweeted = tweet.retweeted;

            // Load in the profile image
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .circleCrop()
                    .into(ivProfileView);

            // If media is present, load in the media image
            if (tweet.mediaURL.length() > 0) {
                // Make the view have spacial dimensions
                ivMedia.getLayoutParams().height = -2;
                ivMedia.getLayoutParams().width = -1;

                // Load in the image
                Glide.with(context)
                        .load(tweet.mediaURL)
                        .fitCenter()
                        .apply(new RequestOptions().transform(new RoundedCorners(50)))
                        .into(ivMedia);

                // Make the view visible
                ivMedia.setVisibility(View.VISIBLE);
            }
            // If media is not present, make the view invisible
            else {
                //
                ivMedia.getLayoutParams().height = 0;
                ivMedia.getLayoutParams().width = 0;
                ivMedia.setVisibility(View.INVISIBLE);
            }

            // If the tweet is favorited, load in the faorited icon
            if (tweet.favorited == true) {
                Glide.with(context)
                        .load(R.drawable.heart_filled)
                        .into(like);
            }
            else {
                Glide.with(context)
                        .load(R.drawable.heart)
                        .into(like);
            }

            // If the tweet is retweeted, load in the retweeted icon
            if (tweet.retweeted == true) {
                Glide.with(context)
                        .load(R.drawable.retweet_filled)
                        .into(retweet);
            }
            else {
                Glide.with(context)
                        .load(R.drawable.retweet)
                        .into(retweet);
            }
        }

    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }




    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}
