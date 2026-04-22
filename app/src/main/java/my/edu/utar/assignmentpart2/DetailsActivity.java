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

        // Initialize the Places SDK
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            // Replace this with the exact API key from your Google Cloud Console
            com.google.android.libraries.places.api.Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), "AIzaSyCj9zf6RkkQh_bx23N2QHtG4BFqZzaM_Bc");
        }

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
        // 1. Create the Places Client
        com.google.android.libraries.places.api.net.PlacesClient placesClient =
                com.google.android.libraries.places.api.Places.createClient(this);

        // 2. Define what data we want Google to return (We just want the Reviews)
        java.util.List<com.google.android.libraries.places.api.model.Place.Field> placeFields =
                java.util.Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.REVIEWS);

        // 3. Search for the place using the Name and City
        String searchQuery = name + " " + city;

        // --- FIXED: Pass both the searchQuery and placeFields into the builder ---
        com.google.android.libraries.places.api.net.SearchByTextRequest searchRequest =
                com.google.android.libraries.places.api.net.SearchByTextRequest.builder(searchQuery, placeFields)
                        .setMaxResultCount(1)
                        .build();

        // 4. Send the request to Google
        placesClient.searchByText(searchRequest).addOnSuccessListener(response -> {
            java.util.List<com.google.android.libraries.places.api.model.Place> places = response.getPlaces();

            if (!places.isEmpty()) {
                com.google.android.libraries.places.api.model.Place place = places.get(0);
                java.util.List<com.google.android.libraries.places.api.model.Review> reviews = place.getReviews();

                if (reviews != null && !reviews.isEmpty()) {
                    // Loop through the reviews and add them to your UI
                    for (com.google.android.libraries.places.api.model.Review review : reviews) {
                        addReviewToUI(review.getAuthorAttribution().getName(), review.getRating(), review.getText());
                    }
                } else {
                    addReviewToUI("System", 0, "No reviews found for this location yet.");
                }
            } else {
                addReviewToUI("System", 0, "Could not locate this place on Google Maps.");
            }
        }).addOnFailureListener(e -> {
            addReviewToUI("Error", 0, "Failed to load reviews: " + e.getMessage());
        });
    }

    // Helper method to build the visual review boxes
    private void addReviewToUI(String author, double rating, String text) {
        TextView reviewBox = new TextView(this);

        // Convert the number rating (e.g., 4.0) into star emojis
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < (int) rating; i++) {
            stars.append("⭐");
        }

        reviewBox.setText(stars.toString() + "\n\"" + text + "\"\n- " + author);
        reviewBox.setPadding(24, 24, 24, 24);
        reviewBox.setTextSize(14f);
        reviewBox.setTextColor(android.graphics.Color.DKGRAY);

        // Add a small margin between reviews
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        reviewBox.setLayoutParams(params);

        reviewBox.setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"));

        llReviewsContainer.addView(reviewBox);
    }
}