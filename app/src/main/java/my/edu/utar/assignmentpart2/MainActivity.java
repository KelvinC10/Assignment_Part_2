package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

// Main landing page of the application.
// Handles the dashboard display, global search functionality, and Firebase data retrieval.

public class MainActivity extends AppCompatActivity {

    // UI Components and Adapters
    private MainActivityAdapter adapterAttractions, adapterLocal, adapterFood;
    private List<LocationModel> listAttractions, listLocal, listFood;
    private RecyclerView rvAttractions, rvLocal, rvFood, rvSearch;
    private FirebaseFirestore db;
    private android.widget.LinearLayout llNoResult;
    private EditText etSearch;
    private SearchAdapter searchAdapter;

    // Master lists to store all searchable data for the global search feature
    private List<LocationModel> masterSearchList = new ArrayList<>();
    private List<String> masterSearchTypes = new ArrayList<>();

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

        // 1. Display Current Date
        TextView tvDate = findViewById(R.id.tvDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String dateToShow = getString(R.string.date_format, currentDate);
        tvDate.setText(dateToShow);

        // Connect to Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Search Elements
        etSearch = findViewById(R.id.etSearch);
        rvSearch = findViewById(R.id.rvSearch);
        llNoResult = findViewById(R.id.llNoResult);

        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(this, new ArrayList<>(), new ArrayList<>());
        rvSearch.setAdapter(searchAdapter);

        // Activate search bar listeners and fetch background data for search
        setupSearchBar();
        fetchGlobalSearchData();

        // Setup THE THREE CATEGORIES: Horizontal RecyclerViews
        setupRecyclerViews();

        // Fetch Data from Firestore Collections
        loadCollection("Best Attraction Places", listAttractions, adapterAttractions);
        loadCollection("Local Recommendation Places", listLocal, adapterLocal);
        loadCollection("Food", listFood, adapterFood);

        // Bottom Navigation
        setupBottomNavigation();

        // Setup Favourite Heart(Wishlist) Icon Click
        ImageView ivHeart = findViewById(R.id.ivHeart);
        ivHeart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavouriteList.class);
            startActivity(intent);
        });

        // Setup Weather Icon Click
        View weatherButton = findViewById(R.id.ivWeather);
        weatherButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
        });

        // Load saved favorites from local phone storage(SharedPreferences)
        FavouriteManager.init(this);
    }

    // Setup horizontal list containers for Best Attractions, Local Recommendations, and Food.
    private void setupRecyclerViews() {
        // Best Attractions
        listAttractions = new ArrayList<>();
        // Add "Best" as the second parameter
        adapterAttractions = new MainActivityAdapter(listAttractions, "Best");
        rvAttractions = findViewById(R.id.rvBestAttraction);
        rvAttractions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAttractions.setAdapter(adapterAttractions);

        // Local Recommendations
        listLocal = new ArrayList<>();
        // Add "Local" as the second parameter
        adapterLocal = new MainActivityAdapter(listLocal, "Local");
        rvLocal = findViewById(R.id.rvLocalRec);
        rvLocal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvLocal.setAdapter(adapterLocal);

        // Food
        listFood = new ArrayList<>();
        // Add "Food" as the second parameter
        adapterFood = new MainActivityAdapter(listFood, "Food");
        rvFood = findViewById(R.id.rvFood);
        rvFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFood.setAdapter(adapterFood);
    }

    // Fetch data from Firestore and update specific adapters.
    private void loadCollection(String collectionName, List<LocationModel> list, MainActivityAdapter adapter) {
        db.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        list.add(item);
                    }
                    adapter.notifyDataSetChanged(); // Refresh UI after data download
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load " + collectionName, Toast.LENGTH_SHORT).show();
                });
    }

    // Navigation bar for switching between Activities
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

    // Real time filter System
    private void setupSearchBar() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString()); // Perform search filtering as user types
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    // Compares user query against the master list and toggles UI visibility based on results.
    private void filterSearch(String query) {
        // If search bar is empty, hide everything
        if (query.trim().isEmpty()) {
            rvSearch.setVisibility(android.view.View.GONE);
            llNoResult.setVisibility(android.view.View.GONE);
            return;
        }

        List<LocationModel> filteredList = new ArrayList<>();
        List<String> filteredTypes = new ArrayList<>();

        // Scan the Global Master List
        for (int i = 0; i < masterSearchList.size(); i++) {
            LocationModel item = masterSearchList.get(i);

            // If the name contains the typed letters (ignoring uppercase/lowercase)
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
                filteredTypes.add(masterSearchTypes.get(i));
            }
        }

        // Show Results OR Show "Doesn't Exist" Layout
        if (filteredList.isEmpty()) {
            rvSearch.setVisibility(android.view.View.GONE);
            llNoResult.setVisibility(android.view.View.VISIBLE);
        } else {
            searchAdapter.updateList(filteredList, filteredTypes);
            rvSearch.setVisibility(android.view.View.VISIBLE);
            llNoResult.setVisibility(android.view.View.GONE);
        }
    }
    private void fetchGlobalSearchData() {
        masterSearchList.clear();
        masterSearchTypes.clear();

        // 1. Fetch from Location Best Attractions
        db.collection("Location Best Attraction Places").get().addOnSuccessListener(qs -> {
            for (QueryDocumentSnapshot doc : qs) {
                masterSearchList.add(doc.toObject(LocationModel.class));
                masterSearchTypes.add("Location");
            }
        });

        // 2. Fetch from Location Local Recommendations
        db.collection("Location Local Recommendation Places").get().addOnSuccessListener(qs -> {
            for (QueryDocumentSnapshot doc : qs) {
                masterSearchList.add(doc.toObject(LocationModel.class));
                masterSearchTypes.add("Location");
            }
        });

        // 3. Fetch from Food Best Food
        db.collection("Food Page Food").get().addOnSuccessListener(qs -> {
            for (QueryDocumentSnapshot doc : qs) {
                masterSearchList.add(doc.toObject(LocationModel.class));
                masterSearchTypes.add("Food");
            }
        });
    }
}