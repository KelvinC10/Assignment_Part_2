package my.edu.utar.assignmentpart2;

public class LocationModel {
    private String name, description, imageUrl, city;

    // Empty constructor is REQUIRED for Firebase to work
    public LocationModel() {}

    public LocationModel(String name, String description, String imageUrl, String city) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.city = city;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCity() { return city; }
}