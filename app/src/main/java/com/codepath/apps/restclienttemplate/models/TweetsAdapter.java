package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;

import java.io.IOException;
import java.util.List;

// The tweets adapter uses the Recycler View to display the list of tweets
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;


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
        return new ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data/element at the given position
        Tweet tweet = tweets.get(position);

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

        // Given a view to store a tweet, populate that view
        // with tweet information
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get a reference to each item in the tweet view
            ivProfileView = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
        }

        // Given a tweet, save the data form the tweet into this object
        public void bind(Tweet tweet) {
            // Load in the text data
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);

            // Load in the image
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .into(ivProfileView);
        }
    }
}
