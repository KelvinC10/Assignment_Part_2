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

// Activity class that shows detailed information about a selected location or food item.
// It includes images, descriptions, map links, and real-time Google reviews.
public class DetailsActivity extends AppCompatActivity {

    // UI elements for displaying details
    private ImageView ivDetailImage, ivDetailMap, ivDetailFav, ivBackBtn;
    private TextView tvDetailName, tvDetailCity, tvDetailDescription;
    private LinearLayout llReviewsContainer;

    // Variables to store data received from the previous screen
    private String name, city, description, imageUrl, itemType;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable full-screen mode
        setContentView(R.layout.activity_details);

        // Link UI elements
        ivDetailImage = findViewById(R.id.ivDetailImage);
        ivDetailMap = findViewById(R.id.ivDetailMap);
        ivDetailFav = findViewById(R.id.ivDetailFav);
        ivBackBtn = findViewById(R.id.ivBackBtn);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCity = findViewById(R.id.tvDetailCity);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        llReviewsContainer = findViewById(R.id.llReviewsContainer);

        // Get Data passed from the Adapter
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        city = intent.getStringExtra("city");
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");
        itemType = intent.getStringExtra("itemType");
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        // Set the Data to the UI
        tvDetailName.setText(name);
        tvDetailCity.setText(city);
        tvDetailDescription.setText(description);

        // Load the location image using the Glide library
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(ivDetailImage);

        // Favourite System: Check if this item is already saved and handle clicks
        checkFavouriteState();
        ivDetailFav.setOnClickListener(v -> toggleFavourite());

        // 5. Setup Map Button Logic, Open Google Maps when the map icon is clicked
        ivDetailMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DetailsActivity.this, MapsActivity.class);
            mapIntent.putExtra("placeName", name);
            mapIntent.putExtra("city", city);
            mapIntent.putExtra("lat", lat);
            mapIntent.putExtra("lng", lng);
            startActivity(mapIntent);
        });

        // Back Button: Close the details page and return to the previous list
        ivBackBtn.setOnClickListener(v -> {
            finish(); // This instantly closes the Details page and reveals the previous screen
        });

        // Initialize the Places SDK
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            // Replace this with the exact API key from your Google Cloud Console
            com.google.android.libraries.places.api.Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), "AIzaSyCj9zf6RkkQh_bx23N2QHtG4BFqZzaM_Bc");
        }

        // Fetch and display real user reviews from Google
        loadReviews();
    }

    // Checks if the current item is in the favourite list and updates the heart icon.
    private void checkFavouriteState() {
        boolean isFav = false;
        if ("Location".equals(itemType)) {
            isFav = FavouriteManager.isFavLocation(name);
        } else if ("Food".equals(itemType)) {
            isFav = FavouriteManager.isFavFood(name);
        }

        // Switch between filled and empty heart icons
        if (isFav) {
            ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
        } else {
            ivDetailFav.setImageResource(R.drawable.ic_launcher_wishlist_icon);
        }
    }

    // Adds or removes the item from the favourite list when the heart is clicked.
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

    // Uses Google Places API to search for the place and download its reviews
    private void loadReviews() {
        // Create the Places Client
        com.google.android.libraries.places.api.net.PlacesClient placesClient =
                com.google.android.libraries.places.api.Places.createClient(this);

        // Define what data we want Google to return (We just want the Reviews)
        java.util.List<com.google.android.libraries.places.api.model.Place.Field> placeFields =
                java.util.Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.REVIEWS);

        // Search for the place using the Name and City
        String searchQuery = name + " " + city;

        // Pass both the searchQuery and placeFields into the builder
        com.google.android.libraries.places.api.net.SearchByTextRequest searchRequest =
                com.google.android.libraries.places.api.net.SearchByTextRequest.builder(searchQuery, placeFields)
                        .setMaxResultCount(1)
                        .build();

        // Send the request to Google
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

    // Helper method to build a text box in the UI to show a specific review.
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

        // Style the review box with a light background
        reviewBox.setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"));

        llReviewsContainer.addView(reviewBox);
    }
}