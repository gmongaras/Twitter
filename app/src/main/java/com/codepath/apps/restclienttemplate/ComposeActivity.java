package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {

    // Max tweet size is 280 characters
    private static final int MAX_TWEET = 280;

    TextView etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Get the button and text field
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);


        // When the button is clicked, check if the text is empty or too long
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = etCompose.getText();

                // If the tweet is in the proper bounds and is valid
                if (text.length() <= MAX_TWEET &&
                        text.length() > 0) {
                    Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                }
                // If the tweet is outside the proper bounds and is invalid
                else {
                    Toast.makeText(view.getContext(), "Invalid tweet length", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}