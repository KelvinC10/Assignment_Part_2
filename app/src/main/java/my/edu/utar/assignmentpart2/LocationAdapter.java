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

    // Constructor to pass data into the adapter
    public LocationAdapter(Context context, List<LocationModel> locationList, String itemType) {
        this.context = context;
        this.locationList = locationList;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Link the adapter to the XML layout for each row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the specific place data for the current row
        LocationModel location = locationList.get(position);

        holder.tvPlaceName.setText(location.getName());
        holder.tvPlaceCity.setText(location.getCity());
        holder.tvDescription.setText(location.getDescription());

        // Load the image from the internet using Glide library
        Glide.with(context)
                .load(location.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPlace);

        // Check if this item is already in the user's favourite list
        boolean isFavourite = false;
        if (itemType.equals("Location")) {
            isFavourite = FavouriteManager.isFavLocation(location.getName());
        } else if (itemType.equals("Food")) {
            isFavourite = FavouriteManager.isFavFood(location.getName());
        }

        // Show a black heart if it is a favourite, or an empty heart if it is not
        if (isFavourite) {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
        } else {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
        }

        // Logic for when the user clicks the Heart icon
        holder.ivHeartIcon.setOnClickListener(v -> {
            // Safety Check: Stop if the user clicks too fast and the item is already being removed
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            if (itemType.equals("Location")) {
                if (FavouriteManager.isFavLocation(location.getName())) {
                    // If already favourited, remove it
                    FavouriteManager.removeLocation(location.getName());
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();


                    notifyDataSetChanged(); // Refresh list display
                } else {
                    // If not favourited, add it
                    FavouriteManager.addLocation(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            } else if (itemType.equals("Food")) {
                if (FavouriteManager.isFavFood(location.getName())) {
                    FavouriteManager.removeFood(location.getName());
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();

                    // Tell the visual list that the data has shrunk so it doesn't crash!
                    notifyDataSetChanged();
                } else {
                    FavouriteManager.addFood(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Logic for when the user clicks the Map icon
        holder.ivMapIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("placeName", location.getName());
            intent.putExtra("city", location.getCity());
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
            context.startActivity(intent);
        });

        // Logic for when the user clicks the whole card to see more details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            // Pass all the data to the Details screen
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
        // Tell the list how many items to show
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace, ivMapIcon, ivHeartIcon;
        TextView tvPlaceName, tvPlaceCity, tvDescription;

        // This class finds and holds the IDs from the XML layout
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