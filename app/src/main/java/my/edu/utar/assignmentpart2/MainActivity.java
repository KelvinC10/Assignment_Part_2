package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 1. All your variables are declared correctly here
    private MainActivityAdapter adapterAttractions, adapterLocal, adapterFood;
    private List<LocationModel> listAttractions, listLocal, listFood;
    private RecyclerView rvAttractions, rvLocal, rvFood;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Standard System Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Setup Date
        TextView tvDate = findViewById(R.id.tvDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String dateToShow = getString(R.string.date_format, currentDate);
        tvDate.setText(dateToShow);

        // 2. Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // 3. Setup THE THREE CATEGORIES
        setupRecyclerViews();

        // 4. Fetch Data from your 3 Firestore Collections
        // Make sure these collection names match your Firestore exactly!
        loadCollection("Best Attraction Places", listAttractions, adapterAttractions);
        loadCollection("Local Recommendation Places", listLocal, adapterLocal);
        loadCollection("Food", listFood, adapterFood);

        // 5. Bottom Navigation Logic
        setupBottomNavigation();

        // --- Setup Favourite Heart Icon Click ---
        ImageView ivHeart = findViewById(R.id.ivHeart);
        ivHeart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavouriteList.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerViews() {
        // --- Best Attractions ---
        listAttractions = new ArrayList<>();
        // Add "Best" as the second parameter
        adapterAttractions = new MainActivityAdapter(listAttractions, "Best");
        rvAttractions = findViewById(R.id.rvBestAttraction);
        rvAttractions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAttractions.setAdapter(adapterAttractions);

        // --- Local Recommendations ---
        listLocal = new ArrayList<>();
        // Add "Local" as the second parameter
        adapterLocal = new MainActivityAdapter(listLocal, "Local");
        rvLocal = findViewById(R.id.rvLocalRec);
        rvLocal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvLocal.setAdapter(adapterLocal);

        // --- Food ---
        listFood = new ArrayList<>();
        // Add "Food" as the second parameter
        adapterFood = new MainActivityAdapter(listFood, "Food");
        rvFood = findViewById(R.id.rvFood);
        rvFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFood.setAdapter(adapterFood);
    }

    private void loadCollection(String collectionName, List<LocationModel> list, MainActivityAdapter adapter) {
        db.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        list.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load " + collectionName, Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(getApplicationContext(), Location.class));
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