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

    public MainActivityAdapter(List<LocationModel> locationList) {
        this.locationList = locationList;
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
            Intent intent = new Intent(v.getContext(), MapsActivity.class);
            intent.putExtra("placeName", location.getName());
            intent.putExtra("city", location.getCity());
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
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