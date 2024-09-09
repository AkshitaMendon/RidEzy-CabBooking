package com.example.cabbookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ReviewStar extends AppCompatActivity implements View.OnClickListener {
    private int rating = 0;
    private ImageView star1, star2, star3, star4, star5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_star);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        star1.setOnClickListener(this);
        star2.setOnClickListener(this);
        star3.setOnClickListener(this);
        star4.setOnClickListener(this);
        star5.setOnClickListener(this);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.star1:
                rating = 1;
                setStarRating(rating);
                startActivity(new Intent(ReviewStar.this, HomeActivity.class));
                break;
            case R.id.star2:
                rating = 2;
                setStarRating(rating);
                startActivity(new Intent(ReviewStar.this, HomeActivity.class));
                break;
            case R.id.star3:
                rating = 3;
                setStarRating(rating);
                startActivity(new Intent(ReviewStar.this, HomeActivity.class));
                break;
            case R.id.star4:
                rating = 4;
                setStarRating(rating);
                startActivity(new Intent(ReviewStar.this, HomeActivity.class));
                break;
            case R.id.star5:
                rating = 5;
                setStarRating(rating);
                startActivity(new Intent(ReviewStar.this, HomeActivity.class));
                break;
        }
    }
    private void setStarRating(int rating) {
        star1.setImageResource(rating >= 1 ? R.drawable.baseline_star_24 : R.drawable.baseline_star_outline_24);
        star2.setImageResource(rating >= 2 ? R.drawable.baseline_star_24 : R.drawable.baseline_star_outline_24);
        star3.setImageResource(rating >= 3 ? R.drawable.baseline_star_24 : R.drawable.baseline_star_outline_24);
        star4.setImageResource(rating >= 4 ? R.drawable.baseline_star_24 : R.drawable.baseline_star_outline_24);
        star5.setImageResource(rating >= 5 ? R.drawable.baseline_star_24 : R.drawable.baseline_star_outline_24);

        // Display a toast message with the selected rating
        Toast.makeText(this, "Ratings submitted: " + rating, Toast.LENGTH_SHORT).show();
    }
}