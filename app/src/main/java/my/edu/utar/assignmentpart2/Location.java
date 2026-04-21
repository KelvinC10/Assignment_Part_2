package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView; // Ensure this is imported
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Location extends AppCompatActivity {

    private RecyclerView rvBestVertical, rvLocalVertical;
    private LocationAdapter adapterBest, adapterLocal;
    private List<LocationModel> listBest, listLocal;
    private FirebaseFirestore db;
    private NestedScrollView nestedScrollView; // Needed for smooth scrolling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        // Find the ScrollView (Make sure your XML ID matches this)
        nestedScrollView = findViewById(R.id.nestedScrollView);

        // 1. Setup Best Attractions
        rvBestVertical = findViewById(R.id.rvBestVertical);
        rvBestVertical.setLayoutManager(new LinearLayoutManager(this));
        // Disabling nested scrolling on RV allows the NestedScrollView to handle it smoothly
        rvBestVertical.setNestedScrollingEnabled(false);
        listBest = new ArrayList<>();
        adapterBest = new LocationAdapter(this, listBest, "Location");
        rvBestVertical.setAdapter(adapterBest);

        // 2. Setup Local Recommendations
        rvLocalVertical = findViewById(R.id.rvLocalVertical);
        rvLocalVertical.setLayoutManager(new LinearLayoutManager(this));
        rvLocalVertical.setNestedScrollingEnabled(false);
        listLocal = new ArrayList<>();
        adapterLocal = new LocationAdapter(this, listLocal, "Location");
        rvLocalVertical.setAdapter(adapterLocal);

        // 3. Fetch Data
        fetchLocationData();

        // --- NEW: AUTO-SCROLL LOGIC ---
        handleIncomingIntent();

        // 4. Bottom Navigation Logic (Keep your existing code)
        setupBottomNavigation();
    }

    // Inside your Location.java

    private void handleIncomingIntent() {
        String category = getIntent().getStringExtra("CATEGORY_KEY");

        if (category != null && nestedScrollView != null) {
            // We use post() to wait for the UI to exist
            nestedScrollView.post(() -> {
                if (category.equals("Local")) {
                    // 1. Force the layout to calculate where everything is
                    rvLocalVertical.requestLayout();

                    // 2. Use a small delay to allow the cards to "inflate"
                    nestedScrollView.postDelayed(() -> {
                        // Get the exact location of the Local Recommendation header/RV
                        int scrollToY = rvLocalVertical.getTop();

                        // 3. Jump or Smooth Scroll to that exact Y position
                        nestedScrollView.smoothScrollTo(0, scrollToY);

                    }, 100); // 100ms is enough to be "instant" but safe
                }
            });
        }
    }

    private void fetchLocationData() {
        db.collection("Location Best Attraction Places").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listBest.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        listBest.add(item);
                    }
                    adapterBest.notifyDataSetChanged();

                    // If the user clicked "Best", check if we should scroll now
                    checkAndScroll("Best");
                });

        db.collection("Location Local Recommendation Places").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listLocal.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        listLocal.add(item);
                    }
                    adapterLocal.notifyDataSetChanged();

                    // If the user clicked "Local", check if we should scroll now
                    checkAndScroll("Local");
                });
    }

    private void checkAndScroll(String currentLoadedCategory) {
        String targetCategory = getIntent().getStringExtra("CATEGORY_KEY");

        // If the category we just loaded is the one the user clicked, perform the scroll
        if (targetCategory != null && targetCategory.equals(currentLoadedCategory)) {
            nestedScrollView.postDelayed(() -> {

                // Sequence: Best first, then Local
                if (targetCategory.equals("Best")) {
                    nestedScrollView.smoothScrollTo(0, rvBestVertical.getTop());
                    Toast.makeText(this, "Welcome to Best Attractions Category", Toast.LENGTH_SHORT).show();

                } else if (targetCategory.equals("Local")) {
                    // Calculation: scroll to the top position of the second RecyclerView
                    int scrollToY = rvLocalVertical.getTop();
                    nestedScrollView.smoothScrollTo(0, scrollToY);
                    Toast.makeText(this, "Welcome to Local Recommendations Category", Toast.LENGTH_SHORT).show();
                }

                // Important: Clear the intent so it doesn't re-toast if the user rotates the screen
                getIntent().removeExtra("CATEGORY_KEY");

            }, 150); // Small delay to ensure the UI has finished drawing the new cards
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_location);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_location) return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            if (id == R.id.nav_food) {
                startActivity(new Intent(getApplicationContext(), Food.class));
                overridePendingTransition(0, 0);
                return true;
            }
            if (id == R.id.nav_AI) {
                startActivity(new Intent(getApplicationContext(), AI.class));
                overridePendingTransition(0, 0);
                return true;
            }
            if (id == R.id.nav_more) {
                startActivity(new Intent(getApplicationContext(), More.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}