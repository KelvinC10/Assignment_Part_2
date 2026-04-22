package my.edu.utar.assignmentpart2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<LocationModel> locationList;
    private Context context;
    private String itemType; // "Location" or "Food"

    public LocationAdapter(Context context, List<LocationModel> locationList, String itemType) {
        this.context = context;
        this.locationList = locationList;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel location = locationList.get(position);

        holder.tvPlaceName.setText(location.getName());
        holder.tvPlaceCity.setText(location.getCity());
        holder.tvDescription.setText(location.getDescription());

        Glide.with(context)
                .load(location.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPlace);

        boolean isFavourite = false;
        if (itemType.equals("Location")) {
            isFavourite = FavouriteManager.isFavLocation(location.getName());
        } else if (itemType.equals("Food")) {
            isFavourite = FavouriteManager.isFavFood(location.getName());
        }

        if (isFavourite) {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
        } else {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
        }

        holder.ivHeartIcon.setOnClickListener(v -> {
            if (itemType.equals("Location")) {
                if (FavouriteManager.isFavLocation(location.getName())) {
                    FavouriteManager.favLocations.removeIf(loc -> loc.getName().equals(location.getName()));
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteManager.favLocations.add(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            } else if (itemType.equals("Food")) {
                if (FavouriteManager.isFavFood(location.getName())) {
                    FavouriteManager.favFoods.removeIf(loc -> loc.getName().equals(location.getName()));
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteManager.favFoods.add(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.ivMapIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("placeName", location.getName());
            intent.putExtra("city", location.getCity());
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
            context.startActivity(intent);
        });

        // --- NEW: Make the whole card clickable to open DetailsActivity ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            // Pass all the data
            intent.putExtra("name", location.getName());
            intent.putExtra("city", location.getCity());
            intent.putExtra("description", location.getDescription());
            intent.putExtra("imageUrl", location.getImageUrl());
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
            intent.putExtra("itemType", itemType); // "Location" or "Food"

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace, ivMapIcon, ivHeartIcon;
        TextView tvPlaceName, tvPlaceCity, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlace = itemView.findViewById(R.id.ivPlaceVertical);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceNameVertical);
            tvPlaceCity = itemView.findViewById(R.id.tvPlaceCityVertical);
            ivMapIcon = itemView.findViewById(R.id.ivMapIcon);
            ivHeartIcon = itemView.findViewById(R.id.ivHeartIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}