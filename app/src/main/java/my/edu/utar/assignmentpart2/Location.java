package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
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
    private NestedScrollView nestedScrollView;

    // The "Gatekeeper" flag
    private boolean hasToasted = false;

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
        nestedScrollView = findViewById(R.id.nestedScrollView);

        // 1. Setup RecyclerViews
        rvBestVertical = findViewById(R.id.rvBestVertical);
        rvBestVertical.setLayoutManager(new LinearLayoutManager(this));
        rvBestVertical.setNestedScrollingEnabled(false);
        listBest = new ArrayList<>();
        adapterBest = new LocationAdapter(this, listBest, "Location");
        rvBestVertical.setAdapter(adapterBest);

        rvLocalVertical = findViewById(R.id.rvLocalVertical);
        rvLocalVertical.setLayoutManager(new LinearLayoutManager(this));
        rvLocalVertical.setNestedScrollingEnabled(false);
        listLocal = new ArrayList<>();
        adapterLocal = new LocationAdapter(this, listLocal, "Location");
        rvLocalVertical.setAdapter(adapterLocal);

        // 2. Fetch Data
        fetchLocationData();

        // 3. Setup Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Reset the gatekeeper for the new click from Home
        hasToasted = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Sync the Bottom Navigation highlight
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_location);
        }

        // Try to scroll if data is already cached
        checkAndScroll("Best");
        checkAndScroll("Local");
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
                    checkAndScroll("Local");
                });
    }

    private void checkAndScroll(String currentLoadedCategory) {
        String targetCategory = getIntent().getStringExtra("CATEGORY_KEY");

        if (targetCategory != null && targetCategory.equals(currentLoadedCategory)) {

            // --- EARLY LOCKING FIX ---
            // If the gate is already locked, stop immediately!
            if (hasToasted) return;

            // Lock the gate NOW before the delay starts
            hasToasted = true;

            nestedScrollView.postDelayed(() -> {
                if (targetCategory.equals("Best") && !listBest.isEmpty()) {
                    nestedScrollView.smoothScrollTo(0, rvBestVertical.getTop());
                    Toast.makeText(this, "Welcome to Best Attractions Category", Toast.LENGTH_SHORT).show();
                    getIntent().removeExtra("CATEGORY_KEY");
                }
                else if (targetCategory.equals("Local") && !listLocal.isEmpty()) {
                    int scrollToY = rvLocalVertical.getTop();
                    nestedScrollView.smoothScrollTo(0, scrollToY);
                    Toast.makeText(this, "Welcome to Local Recommendations Category", Toast.LENGTH_SHORT).show();
                    getIntent().removeExtra("CATEGORY_KEY");
                } else {
                    // If we failed because data wasn't ready, unlock so the next fetch call can try
                    hasToasted = false;
                }
            }, 250);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_location);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_location)
                return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_food) {
                startActivity(new Intent(getApplicationContext(), Food.class));
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