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

public class Location extends AppCompatActivity {

    private RecyclerView rvBestVertical, rvLocalVertical;
    private LocationAdapter adapterBest, adapterLocal;
    private List<LocationModel> listBest, listLocal;
    private FirebaseFirestore db;

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

        // 1. Setup Best Attractions
        rvBestVertical = findViewById(R.id.rvBestVertical);
        rvBestVertical.setLayoutManager(new LinearLayoutManager(this));
        listBest = new ArrayList<>();
        adapterBest = new LocationAdapter(this, listBest, "Location");
        rvBestVertical.setAdapter(adapterBest);

        // 2. Setup Local Recommendations
        rvLocalVertical = findViewById(R.id.rvLocalVertical);
        rvLocalVertical.setLayoutManager(new LinearLayoutManager(this));
        listLocal = new ArrayList<>();
        adapterLocal = new LocationAdapter(this, listLocal, "Location");
        rvLocalVertical.setAdapter(adapterLocal);

        // 3. Fetch Data for both from the NEW collections
        fetchLocationData();

        // 4. Bottom Navigation Logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_location);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_location) {
                return true;
            } else if (id == R.id.nav_home) {
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

    private void fetchLocationData() {
        // Fetch from the new specific Location page collection
        db.collection("Location Best Attraction Places").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listBest.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        listBest.add(item);
                    }
                    adapterBest.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load Best Attractions", Toast.LENGTH_SHORT).show();
                });

        // Fetch from the new specific Location page collection
        db.collection("Location Local Recommendation Places").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listLocal.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LocationModel item = document.toObject(LocationModel.class);
                        listLocal.add(item);
                    }
                    adapterLocal.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load Local Recommendations", Toast.LENGTH_SHORT).show();
                });
    }
}