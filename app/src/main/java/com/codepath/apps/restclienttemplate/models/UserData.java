package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserData {

    private static final String TAG = "UserData";

    // Data to store for each user
    String username;
    String name;
    String imgURL;

    public UserData() {
        username = "";
        name = "";
        imgURL = "";
    }

    public UserData(String username, String name, String imgURL) {
        this.username = username;
        this.name = name;
        this.imgURL = imgURL;
    }

    public static UserData fromJSONObj(JSONObject obj) {
        // Create a new UserData object and return it
        try {
            return new UserData(obj.getString("screen_name"), obj.getString("name"), obj.getString("profile_image_url"));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to get string form JSON object");
            return new UserData();
        }
    }

    public static List<UserData> fromJSONArray(JSONArray array) {
        // Create a list of user data
        List<UserData> UD_list = new ArrayList<>();

        // Iterate over the entire array and load in each object
        for (int i = 0; i < array.length(); i++) {
            try {
                UD_list.add(fromJSONObj(array.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse JSON object from array");
            }
        }

        return UD_list;
    }
}
