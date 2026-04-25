package my.edu.utar.assignmentpart2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// This class manages the local storage for the user's favourite lists.
// It uses SharedPreferences to ensure data is not lost when the app is closed.
public class FavouriteManager {
    // These still hold the items while the app is running
    public static List<LocationModel> favLocations = new ArrayList<>();
    public static List<LocationModel> favFoods = new ArrayList<>();

    // Tools for saving data locally on the phone
    private static SharedPreferences sharedPreferences;
    private static final Gson gson = new Gson(); // Converts Java objects to text (JSON) for saving


    // Initializes the manager and loads any previously saved data from the phone.
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("LocalFavDB", Context.MODE_PRIVATE);
            loadData();
        }
    }

    // Loads the saved JSON text from storage and converts it back into Java Lists.
    private static void loadData() {
        String locationsJson = sharedPreferences.getString("saved_locations", null);
        String foodsJson = sharedPreferences.getString("saved_foods", null);

        // Define the type of data we are retrieving (a list of LocationModels)
        Type type = new TypeToken<ArrayList<LocationModel>>() {}.getType();

        // If data exists, convert the JSON text back into real Java object
        if (locationsJson != null) {
            favLocations = gson.fromJson(locationsJson, type);
        }
        if (foodsJson != null) {
            favFoods = gson.fromJson(foodsJson, type);
        }
    }

    // Converts the current lists into JSON text and saves them to the phone's memory.
    private static void saveData() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("saved_locations", gson.toJson(favLocations));
            editor.putString("saved_foods", gson.toJson(favFoods));
            editor.apply(); // apply() saves in the background so the app doesn't freeze
        }
    }

    // Checks if a specific location is already in the favourite list
    public static boolean isFavLocation(String name) {
        for (LocationModel item : favLocations) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }

    // Checks if a specific food item is already in the favourite list.
    public static boolean isFavFood(String name) {
        for (LocationModel item : favFoods) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }

    // Adds a location to the list and immediately saves the update to the phone.
    public static void addLocation(LocationModel location) {
        favLocations.add(location);
        saveData();
    }

    // Removes a location and updates the phone storage.
    public static void removeLocation(String name) {
        favLocations.removeIf(loc -> loc.getName().equals(name));
        saveData();
    }

    // Adds a food item to the list and immediately saves the update to the phone.
    public static void addFood(LocationModel food) {
        favFoods.add(food);
        saveData();
    }

    // Removes a food item and updates the phone storage.
    public static void removeFood(String name) {
        favFoods.removeIf(loc -> loc.getName().equals(name));
        saveData();
    }
}