package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private ImageView ivDetailImage, ivDetailMap, ivDetailFav, ivBackBtn;
    private TextView tvDetailName, tvDetailCity, tvDetailDescription;
    private LinearLayout llReviewsContainer;

    private String name, city, description, imageUrl, itemType;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        // 1. Link UI elements
        ivDetailImage = findViewById(R.id.ivDetailImage);
        ivDetailMap = findViewById(R.id.ivDetailMap);
        ivDetailFav = findViewById(R.id.ivDetailFav);
        ivBackBtn = findViewById(R.id.ivBackBtn);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCity = findViewById(R.id.tvDetailCity);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        llReviewsContainer = findViewById(R.id.llReviewsContainer);

        // 2. Get Data passed from the Adapter
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        city = intent.getStringExtra("city");
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");
        itemType = intent.getStringExtra("itemType");
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        // 3. Set the Data to the UI
        tvDetailName.setText(name);
        tvDetailCity.setText(city);
        tvDetailDescription.setText(description);
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(ivDetailImage);

        // 4. Setup Favourite Button State and Logic
        checkFavouriteState();
        ivDetailFav.setOnClickListener(v -> toggleFavourite());

        // 5. Setup Map Button Logic
        ivDetailMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DetailsActivity.this, MapsActivity.class);
            mapIntent.putExtra("placeName", name);
            mapIntent.putExtra("city", city);
            mapIntent.putExtra("lat", lat);
            mapIntent.putExtra("lng", lng);
            startActivity(mapIntent);
        });

        ivBackBtn.setOnClickListener(v -> {
            finish(); // This instantly closes the Details page and reveals the previous screen
        });

        // 6. Load Reviews
        loadReviews();
    }

    private void checkFavouriteState() {
        boolean isFav = false;
        if ("Location".equals(itemType)) {
            isFav = FavouriteManager.isFavLocation(name);
        } else if ("Food".equals(itemType)) {
            isFav = FavouriteManager.isFavFood(name);
        }

        if (isFav) {
            ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
        } else {
            ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_icon);
        }
    }

    private void toggleFavourite() {
        LocationModel model = new LocationModel(name, description, imageUrl, city, lat, lng);

        if ("Location".equals(itemType)) {
            if (FavouriteManager.isFavLocation(name)) {
                FavouriteManager.removeLocation(name);
                ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                Toast.makeText(this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
            } else {
                FavouriteManager.addLocation(model);
                ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        } else if ("Food".equals(itemType)) {
            if (FavouriteManager.isFavFood(name)) {
                FavouriteManager.removeFood(name);
                ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                Toast.makeText(this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
            } else {
                FavouriteManager.addFood(model);
                ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadReviews() {
        // NOTE: To get real reviews, you must implement the Google Places SDK.
        // For now, this injects a mock review so your UI looks complete.
        TextView mockReview = new TextView(this);
        mockReview.setText("⭐⭐⭐⭐⭐\n\"Great place to visit! Highly recommend checking this out when you are in " + city + ".\" - Google User");
        mockReview.setPadding(16, 16, 16, 16);
        mockReview.setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"));

        llReviewsContainer.addView(mockReview);
    }
}