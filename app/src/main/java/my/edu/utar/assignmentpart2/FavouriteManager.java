package my.edu.utar.assignmentpart2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavouriteManager {
    // These still hold the items while the app is running
    // Change these from public to private!
    public static List<LocationModel> favLocations = new ArrayList<>();
    public static List<LocationModel> favFoods = new ArrayList<>();

    private static SharedPreferences sharedPreferences;
    private static final Gson gson = new Gson();

    // --- NEW: Initialize the Database ---
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("LocalFavDB", Context.MODE_PRIVATE);
            loadData();
        }
    }

    // --- NEW: Load Data from Phone Storage ---
    private static void loadData() {
        String locationsJson = sharedPreferences.getString("saved_locations", null);
        String foodsJson = sharedPreferences.getString("saved_foods", null);

        Type type = new TypeToken<ArrayList<LocationModel>>() {}.getType();

        if (locationsJson != null) {
            favLocations = gson.fromJson(locationsJson, type);
        }
        if (foodsJson != null) {
            favFoods = gson.fromJson(foodsJson, type);
        }
    }

    // --- NEW: Save Data to Phone Storage ---
    private static void saveData() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("saved_locations", gson.toJson(favLocations));
            editor.putString("saved_foods", gson.toJson(favFoods));
            editor.apply(); // apply() saves in the background so the app doesn't freeze
        }
    }

    // --- Your Original Helper Methods (Unchanged!) ---
    public static boolean isFavLocation(String name) {
        for (LocationModel item : favLocations) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }

    public static boolean isFavFood(String name) {
        for (LocationModel item : favFoods) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }

    // --- UPDATED: Add and Remove Methods (Now they trigger a Save!) ---
    public static void addLocation(LocationModel location) {
        favLocations.add(location);
        saveData();
    }

    public static void removeLocation(String name) {
        favLocations.removeIf(loc -> loc.getName().equals(name));
        saveData();
    }

    public static void addFood(LocationModel food) {
        favFoods.add(food);
        saveData();
    }

    public static void removeFood(String name) {
        favFoods.removeIf(loc -> loc.getName().equals(name));
        saveData();
    }
}