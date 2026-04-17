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

    public LocationAdapter(Context context, List<LocationModel> locationList) {
        this.context = context;
        this.locationList = locationList;
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
        holder.tvDescription.setText(location.getDescription());

        Glide.with(context)
                .load(location.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPlace);

        // Map Icon Click Listener - Opens Bottom Sheet
        holder.ivMapIcon.setOnClickListener(v -> showBottomSheetMap(location.getName()));

        // Heart Icon Click Listener - Placeholder for now
        holder.ivHeartIcon.setOnClickListener(v ->
                Toast.makeText(context, "Added " + location.getName() + " to favorites!", Toast.LENGTH_SHORT).show()
        );
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
            // Intent to open actual Google Maps App
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
        TextView tvPlaceName, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlace = itemView.findViewById(R.id.ivPlaceVertical);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceNameVertical);
            ivMapIcon = itemView.findViewById(R.id.ivMapIcon);
            ivHeartIcon = itemView.findViewById(R.id.ivHeartIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}