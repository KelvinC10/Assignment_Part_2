package my.edu.utar.assignmentpart2;

public class LocationModel {
    private String name, description, imageUrl, category;

    // Empty constructor is REQUIRED for Firebase to work
    public LocationModel() {}

    public LocationModel(String name, String description, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
}