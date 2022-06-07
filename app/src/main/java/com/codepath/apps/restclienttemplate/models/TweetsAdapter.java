package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.detail_tweet;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data/element at the given position
        Tweet tweet = tweets.get(position);

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
        TextView like_ct;

        // Did the user favorite the tweet?
        boolean favorited;

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
            like_ct = itemView.findViewById(R.id.like_ct);
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
