package my.edu.utar.assignmentpart2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

// 1. You must "extend" the RecyclerView.Adapter class here
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    // 2. You must declare the list here so the methods below can see it
    private List<LocationModel> locationList;

    // 3. You need a Constructor to receive the list from MainActivity
    public AttractionAdapter(List<LocationModel> locationList) {
        this.locationList = locationList;
    }

    // 4. You were missing this method! It inflates your location_box.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel location = locationList.get(position);
        holder.tvPlaceName.setText(location.getName());

        // This shows the City (e.g., "Taiping")
        holder.tvPlaceCity.setText(location.getCity());

        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPlace);
    }

    // 5. You were missing this method! It tells the list how many items to show
    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace;
        TextView tvPlaceName, tvPlaceCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlace = itemView.findViewById(R.id.ivPlace);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            // Links to the second TextView in your location_box.xml
            tvPlaceCity = itemView.findViewById(R.id.tvPlaceCategory);
        }
    }
}