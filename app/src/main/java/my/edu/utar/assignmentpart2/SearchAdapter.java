package my.edu.utar.assignmentpart2;

import android.content.Context;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<LocationModel> searchList;
    private List<String> typeList; // Keeps track of whether it's a "Location" or "Food"

    public SearchAdapter(Context context, List<LocationModel> searchList, List<String> typeList) {
        this.context = context;
        this.searchList = searchList;
        this.typeList = typeList;
    }

    // Method to instantly update the list as the user types
    public void updateList(List<LocationModel> filteredList, List<String> filteredTypes) {
        this.searchList = filteredList;
        this.typeList = filteredTypes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel item = searchList.get(position);
        String itemType = typeList.get(position);

        holder.tvSearchName.setText(item.getName());
        holder.tvSearchCity.setText(item.getCity());
        Glide.with(context).load(item.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.ivSearchImage);

        // Direct user to DetailsActivity when clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("city", item.getCity());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("lat", item.getLatitude());
            intent.putExtra("lng", item.getLongitude());
            intent.putExtra("itemType", itemType);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSearchImage;
        TextView tvSearchName, tvSearchCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSearchImage = itemView.findViewById(R.id.ivSearchImage);
            tvSearchName = itemView.findViewById(R.id.tvSearchName);
            tvSearchCity = itemView.findViewById(R.id.tvSearchCity);
        }
    }
}