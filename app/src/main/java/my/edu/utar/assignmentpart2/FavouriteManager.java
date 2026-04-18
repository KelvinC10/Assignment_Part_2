package my.edu.utar.assignmentpart2;

import java.util.ArrayList;
import java.util.List;

public class FavouriteManager {
    // These hold the saved items while the app is running
    public static List<LocationModel> favLocations = new ArrayList<>();
    public static List<LocationModel> favFoods = new ArrayList<>();

    // Helper method to check if a Location is already favourited
    public static boolean isFavLocation(String name) {
        for (LocationModel item : favLocations) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }

    // Helper method to check if a Food is already favourited
    public static boolean isFavFood(String name) {
        for (LocationModel item : favFoods) {
            if (item.getName().equals(name)) return true;
        }
        return false;
    }
}