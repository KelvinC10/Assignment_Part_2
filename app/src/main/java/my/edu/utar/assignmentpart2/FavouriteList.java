package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class FavouriteList extends AppCompatActivity {

    private RecyclerView rvFavouriteList;
    private LocationAdapter adapter;
    private MaterialButton btnToggleLocation, btnToggleFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable full-screen display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourite_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rvFavouriteList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons and the RecyclerView list
        btnToggleLocation = findViewById(R.id.btnToggleLocation);
        btnToggleFood = findViewById(R.id.btnToggleFood);
        rvFavouriteList = findViewById(R.id.rvFavouriteList);
        rvFavouriteList.setLayoutManager(new LinearLayoutManager(this));

        // Start by showing Locations by default
        showLocationFavourites();

        // Button Click Listeners
        btnToggleLocation.setOnClickListener(v -> showLocationFavourites());
        btnToggleFood.setOnClickListener(v -> showFoodFavourites());

        // Bottom Navigation Logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Since FavouriteList isn't one of the 4 main tabs, uncheck all of them so none look "active"
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        // Navigation bar for switching between Activities
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
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

    // Refreshes the list every time the user returns to this screen.
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Logic to display favourite sightseeing locations.
    // Updates button colors to show 'Locations' is active.
    private void showLocationFavourites() {
        // Active Button: Dark Grey Background, White Text
        btnToggleLocation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#424242")));
        btnToggleLocation.setTextColor(Color.WHITE);

        // Inactive Button: Light Grey Background, Black Text (So it never disappears!)
        btnToggleFood.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
        btnToggleFood.setTextColor(Color.BLACK);

        // Load the saved locations into the list
        adapter = new LocationAdapter(this, FavouriteManager.favLocations, "Location");
        rvFavouriteList.setAdapter(adapter);
    }

    // Logic to display favourite food items.
    // Updates button colors to show 'Food' is active.
    private void showFoodFavourites() {
        // Active Button: Dark Grey Background, White Text
        btnToggleFood.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#424242")));
        btnToggleFood.setTextColor(Color.WHITE);

        // Inactive Button: Light Grey Background, Black Text
        btnToggleLocation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
        btnToggleLocation.setTextColor(Color.BLACK);

        // Load the saved foods into the list
        adapter = new LocationAdapter(this, FavouriteManager.favFoods, "Food");
        rvFavouriteList.setAdapter(adapter);
    }
}