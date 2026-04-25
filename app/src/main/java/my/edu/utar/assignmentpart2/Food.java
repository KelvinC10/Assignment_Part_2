package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


// Fetching food data from Firebase and handles user navigation.
public class Food extends AppCompatActivity {

    private RecyclerView rvFoodVertical;
    private LocationAdapter adapterFood;
    private List<LocationModel> listFood;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable full-screen display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase Firestore connection
        db = FirebaseFirestore.getInstance();

        // 1. Setup RecyclerView for food items
        rvFoodVertical = findViewById(R.id.rvFoodVertical);
        rvFoodVertical.setLayoutManager(new LinearLayoutManager(this));
        listFood = new ArrayList<>();
        adapterFood = new LocationAdapter(this, listFood, "Food");
        rvFoodVertical.setAdapter(adapterFood);

        // 2. Fetch Data from Firestore
        fetchFoodData();

        // 3. Setup Bottom Navigation
        setupBottomNavigation();
    }

    // --- NEW: Handles the intent if the activity is already open ---
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Replace old intent with the new one from MainActivity
    }

    // Runs every time the user comes back to this screen.
    // Keeps the UI synchronized and updated.
    @Override
    protected void onResume() {
        super.onResume();

        // Ensure the 'Food' icon is highlighted in the bottom menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_food);
        }

        // Check if we need to show a welcome message
        handleFoodIntent();

        // Refresh the list to ensure the heart (favourite) icons match the current status
        if (adapterFood != null) {
            adapterFood.notifyDataSetChanged();
        }
    }

    // Displays a welcome Toast if the user navigated here from the Home Dashboard.
    private void handleFoodIntent() {
        String category = getIntent().getStringExtra("CATEGORY_KEY");
        if ("Food".equals(category)) {
            Toast.makeText(this, "Welcome to Perak Food Paradise!", Toast.LENGTH_SHORT).show();
            // Clear the key so it doesn't toast again on screen rotation
            getIntent().removeExtra("CATEGORY_KEY");
        }
    }

    // Connects to Firebase to pull the list of local food
    private void fetchFoodData() {
        db.collection("Food Page Food").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listFood.clear(); // Clear old data to prevent duplicates
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        listFood.add(item);
                    }
                    adapterFood.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Show error if internet connection fails
                    Toast.makeText(this, "Failed to load Food data", Toast.LENGTH_SHORT).show();
                });
    }

    // Navigation bar for switching between Activities
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_food);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_food)
                return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(getApplicationContext(), Location.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_AI) {
                startActivity(new Intent(getApplicationContext(), AI.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_more) {
                startActivity(new Intent(getApplicationContext(), More.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}