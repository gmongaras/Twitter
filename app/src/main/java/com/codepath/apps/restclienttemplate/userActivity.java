package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.UserData;
import com.codepath.apps.restclienttemplate.models.UserDataAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity {

    private static final String TAG = "userActivity";

    // The user data
    JSONArray users;

    // User data in a better form
    List<UserData> userList;

    // Used to work with the RV
    UserDataAdapter adapter;

    // Elements in the user activity
    ImageView profileImg;
    TextView username;
    RecyclerView rvUserActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Get the user data
        try {
            users = new JSONArray(getIntent().getExtras().getString("users"));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to get user data");
        }

        // Get the elements
        profileImg = findViewById(R.id.profileImg);
        username = findViewById(R.id.username_ud);
        rvUserActivity = findViewById(R.id.rvUserActivity);

        // Bind data to the elements
        username.setText(getIntent().getExtras().getString("username"));
        Glide.with(profileImg)
                .load(getIntent().getExtras().getString("profileImg"))
                .circleCrop()
                .into(profileImg);

        // Initialize the other objects
        userList = new ArrayList<>();
        adapter = new UserDataAdapter(rvUserActivity.getContext(), userList);

        // Configure the Recycler View: Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUserActivity.setLayoutManager(linearLayoutManager);

        // Configure the Recycler View: Adapter
        rvUserActivity.setAdapter(adapter);

        // Populate the recycler view
        populateRV();
    }


    public void populateRV() {
        // Populate the user data
        userList.addAll(UserData.fromJSONArray(users));

        // Notify the adapter of the dataset change
        adapter.notifyDataSetChanged();
    }
}