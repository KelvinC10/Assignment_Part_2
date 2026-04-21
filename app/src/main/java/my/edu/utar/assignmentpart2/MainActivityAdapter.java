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

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    private List<LocationModel> locationList;
    private String category;

    public MainActivityAdapter(List<LocationModel> locationList, String category) {
        this.locationList = locationList;
        this.category = category;
    }

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
        holder.tvPlaceCity.setText(location.getCity());

        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .into(holder.ivPlace);

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

            // 3. Add flags so the Activity "refreshes" properly if it was already open
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            v.getContext().startActivity(intent);
        });
    }

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
            tvPlaceCity = itemView.findViewById(R.id.tvCity);
        }
    }
}