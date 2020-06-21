package com.example.parkgnv;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AfterParking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_parking);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AfterParkingController ratingController = new AfterParkingController(ratingBar.getRating(), 1);
                /*Snackbar.make(view, Float.toString(ratingBar.getRating()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                finish();

            }
        });
    }
}