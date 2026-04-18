package my.edu.utar.assignmentpart2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<LocationModel> locationList;
    private Context context;
    private String itemType; // NEW: "Location" or "Food"

    // NEW: Update constructor to accept the itemType
    public LocationAdapter(Context context, List<LocationModel> locationList, String itemType) {
        this.context = context;
        this.locationList = locationList;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel location = locationList.get(position);

        holder.tvPlaceName.setText(location.getName());
        holder.tvPlaceCity.setText(location.getCity());
        holder.tvDescription.setText(location.getDescription());

        Glide.with(context).load(location.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.ivPlace);

        // --- NEW: Check if item is favourited and set correct icon ---
        boolean isFavourite = false;
        if (itemType.equals("Location")) {
            isFavourite = FavouriteManager.isFavLocation(location.getName());
        } else if (itemType.equals("Food")) {
            isFavourite = FavouriteManager.isFavFood(location.getName());
        }

        if (isFavourite) {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon); // Your filled icon
        } else {
            holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon); // Your empty icon
        }

        // --- NEW: Heart Icon Click Logic ---
        holder.ivHeartIcon.setOnClickListener(v -> {
            if (itemType.equals("Location")) {
                if (FavouriteManager.isFavLocation(location.getName())) {
                    // Remove it
                    FavouriteManager.favLocations.removeIf(loc -> loc.getName().equals(location.getName()));
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    // Add it
                    FavouriteManager.favLocations.add(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            } else if (itemType.equals("Food")) {
                if (FavouriteManager.isFavFood(location.getName())) {
                    // Remove it
                    FavouriteManager.favFoods.removeIf(loc -> loc.getName().equals(location.getName()));
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_icon);
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    // Add it
                    FavouriteManager.favFoods.add(location);
                    holder.ivHeartIcon.setImageResource(R.drawable.ic_launcher_wishlist_fill_icon);
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Map Icon Logic (Unchanged)
        holder.ivMapIcon.setOnClickListener(v -> showBottomSheetMap(location.getName()));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    private void showBottomSheetMap(String locationName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_map, null);
        bottomSheetDialog.setContentView(view);

        Button btnOpenMap = view.findViewById(R.id.btnOpenGoogleMaps);
        btnOpenMap.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(locationName + " Perak"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
            }
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
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