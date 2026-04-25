package my.edu.utar.assignmentpart2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


// Adapter class for the Horizontal RecyclerViews in MainActivity.
// Handles displaying Best Attractions, Local Recommendations, and Food categories.
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    private List<LocationModel> locationList;
    private String category; // Stores "Best", "Local", or "Food" to handle navigation logic

    // Constructor to initialize data list and category type
    public MainActivityAdapter(List<LocationModel> locationList, String category) {
        this.locationList = locationList;
        this.category = category;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout (location_box.xml) for individual items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on the current position
        LocationModel location = locationList.get(position);
        holder.tvPlaceName.setText(location.getName());
        holder.tvPlaceCity.setText(location.getCity());

        // Use Glide library to load image from Firebase URL into the ImageView
        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .into(holder.ivPlace);

        // Set click listener for the entire item card
        holder.itemView.setOnClickListener(v -> {
            Intent intent;

            // 1. Decide which Activity to go to
            if ("Food".equals(category)) {
                intent = new Intent(v.getContext(), Food.class);
            } else {
                intent = new Intent(v.getContext(), Location.class);
            }

            // 2. ALWAYS pass the category key so the target page can Toast/Scroll
            intent.putExtra("CATEGORY_KEY", category);

            // 3. Flag Management: Ensure the Activity refreshes correctly if it's already in the background
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Start the transition to the next screen
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return locationList.size();
    }


    // ViewHolder class to hold and cache view references for better performance
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace;
        TextView tvPlaceName, tvPlaceCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Linking variables to XML IDs
            ivPlace = itemView.findViewById(R.id.ivPlace);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvPlaceCity = itemView.findViewById(R.id.tvCity);
        }
    }
}