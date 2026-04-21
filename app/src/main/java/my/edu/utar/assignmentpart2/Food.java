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

public class Food extends AppCompatActivity {

    private RecyclerView rvFoodVertical;
    // We are reusing the Location adapter and model here!
    private LocationAdapter adapterFood;
    private List<LocationModel> listFood;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        // 1. Setup RecyclerView
        rvFoodVertical = findViewById(R.id.rvFoodVertical);
        rvFoodVertical.setLayoutManager(new LinearLayoutManager(this));
        listFood = new ArrayList<>();

        // Reusing LocationVerticalAdapter since the layout is identical
        adapterFood = new LocationAdapter(this, listFood, "Food");
        rvFoodVertical.setAdapter(adapterFood);

        // 2. Fetch Data from the new Food collection
        fetchFoodData();

        // 3. Bottom Navigation Logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_food);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_food) {
                return true; // Already here
            } else if (id == R.id.nav_home) {
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

    private void fetchFoodData() {
        // Fetching specifically from your new Food collection
        db.collection("Food Page Food").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listFood.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Firebase maps the food data perfectly into the LocationModel
                        LocationModel item = document.toObject(LocationModel.class);
                        listFood.add(item);
                    }
                    adapterFood.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load Food data", Toast.LENGTH_SHORT).show();
                });
    }
}