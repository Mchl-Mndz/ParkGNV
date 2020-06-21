package com.example.parkgnv;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.RatingBar;

public class AfterParking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_parking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AfterParkingController ratingController = new AfterParkingController(ratingBar.getRating(), 1);
                Snackbar.make(view, Float.toString(ratingBar.getRating()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();

            }
        });
    }
}