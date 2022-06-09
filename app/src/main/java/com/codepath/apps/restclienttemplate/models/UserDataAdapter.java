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

import java.util.List;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder> {
    Context context;
    List<UserData> userDataList;
    private static final String TAG = "UserDataAdapter";

    // Pass in the context and list of tweets as a constructor
    public UserDataAdapter(Context context, List<UserData> userDataList) {
        this.context = context;
        this.userDataList = userDataList;
    }


    // For each row, inflate the layout for a user
    @NonNull
    @Override
    public UserDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view and inflate it in the Recycler View
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);

        // Return the view
        return new UserDataAdapter.ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull UserDataAdapter.ViewHolder holder, int position) {
        // Get the data at the given position
        UserData user = userDataList.get(position);

        // Bind the user to the view holder
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    // Define a view holder to store information about the user data layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        // References to each item in the view
        TextView name_ud;
        TextView username_ud;
        ImageView profileImg_ud;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get a reference to each item in the view
            name_ud = itemView.findViewById(R.id.name_ud);
            username_ud = itemView.findViewById(R.id.username_ud);
            profileImg_ud = itemView.findViewById(R.id.profileImg_ud);
        }

        // Given a UserData object, bind data to the view
        public void bind(UserData user) {
            // Bind the text data
            name_ud.setText(user.name);
            username_ud.setText("@" + user.username);

            // Ensure the URL starts with https
            if (!user.imgURL.substring(0, 5).equals("https")) {
                user.imgURL = "https" + user.imgURL.substring(4);
            }

            // Bind the profile image
            Glide.with(context)
                    .load(user.imgURL)
                    .circleCrop()
                    .into(profileImg_ud);
        }
    }
}
